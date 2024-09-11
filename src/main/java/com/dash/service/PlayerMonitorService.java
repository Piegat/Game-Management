package com.dash.service;

import com.dash.entity.Player;
import com.dash.entity.Server;
import com.dash.repository.PlayerRepository;
import com.dash.repository.ServerRepository;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;

import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class PlayerMonitorService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ServerRepository serverRepository;

    private InfluxDB influxDB;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<String, Boolean> playerStatus = new HashMap<>();

    @Scheduled(fixedRate = 15000)  // Verifica a cada 15 segundos
    public void checkPlayerStatus() {
        List<Server> servers = serverRepository.findAll();

        for (Server server : servers) {
            try {
                String baseUrl = "https://servers-frontend.fivem.net/api/servers/single/";
                String url = baseUrl + server.getIp();

                JsonNode response = restTemplate.getForObject(url, JsonNode.class);

                JsonNode playersList = response.path("Data").path("players");

                List<Player> playerList = playerRepository.findAll();

                for (Player dbPlayer : playerList) {
                    String playerLicense = dbPlayer.getLicense();
                    boolean isPlayerOnline = false;

                    // Verifica se o jogador está na lista do servidor (online)
                    for (JsonNode player : playersList) {
                        for (JsonNode identifier : player.path("identifiers")) {
                            if (identifier.asText().startsWith("license:") && identifier.asText().equals("license:" + playerLicense)) {
                                System.out.println(player.path("name").asText());
                                isPlayerOnline = true;
                                break;
                            }
                        }
                        if (isPlayerOnline) {
                            break;
                        }
                    }

                    // A cada execução, registra o status atual (online/offline)
                    logPlayerStatus(dbPlayer.getLicense(), isPlayerOnline, server.getName(), dbPlayer.getName());
                    playerStatus.put(playerLicense, isPlayerOnline); // Atualiza o status
                }

            } catch (Exception e) {
                System.out.println("Erro ao fazer requisição: " + e.getMessage());
            }
        }
    }

    private void logPlayerStatus(String playerLicense, boolean isOnline, String serverName, String name) {

        BatchPoints.Builder batchPointsBuilder = BatchPoints.database("gaming");

        Point point = Point.measurement("player_online")
                .tag("server", serverName)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("name", name)
                .addField("player_license", playerLicense)
                .addField("status", isOnline ? "online" : "offline")
                .build();

                batchPointsBuilder.point(point);

        influxDB.write(batchPointsBuilder.build());

    }

}
