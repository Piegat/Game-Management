package com.dash.service;

import com.dash.entity.Player;
import com.dash.entity.Server;
import com.dash.repository.PlayerRepository;
import com.dash.repository.ServerRepository;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;

import org.influxdb.dto.Point;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class PlayerMonitorService {

    private final PlayerRepository playerRepository;
    private final ServerRepository serverRepository;
    private final InfluxDB influxDB;
    private final RestTemplate restTemplate = new RestTemplate();


    public PlayerMonitorService(PlayerRepository playerRepository, ServerRepository serverRepository, InfluxDB influxDB) {
        this.playerRepository = playerRepository;
        this.serverRepository = serverRepository;
        this.influxDB = influxDB;
    }

    @Scheduled(fixedRate = 30000)
    public void checkPlayerStatus() {
        List<Server> servers = serverRepository.findAll();
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        int count = 1;

        for (Server server : servers) {
            try {
                System.out.println("Servidores verificados: " + count + " Total de servidores: " + servers.size());

                String baseUrl = "https://servers-frontend.fivem.net/api/servers/single/";
                String url = baseUrl + server.getIp();
                JsonNode response = restTemplate.getForObject(url, JsonNode.class);
                JsonNode playersList = response.path("Data").path("players");

                List<Player> playerList = playerRepository.findAll();

                for (Player dbPlayer : playerList) {
                    String playerLicense = dbPlayer.getLicense();
                    boolean isPlayerOnline = false;

                    for (JsonNode player : playersList) {
                        for (JsonNode identifier : player.path("identifiers")) {
                            if (identifier.asText().startsWith("license:") && identifier.asText().equals("license:" + playerLicense)) {
                                isPlayerOnline = true;
                                break;
                            }
                        }
                        if (isPlayerOnline) {
                            break;
                        }
                    }

                    logPlayerStatus(isPlayerOnline, server.getName(), dbPlayer.getName(), timeUnit);

                }

                Thread.sleep(1000);
                count += 1;

            } catch (Exception e) {
                System.out.println("Erro ao fazer requisição: " + e.getMessage());
            }
        }
    }


    private void logPlayerStatus(boolean isOnline, String serverName, String name, TimeUnit timeUnit) {
        BatchPoints.Builder batchPointsBuilder = BatchPoints.database("gaming");
        Point point = Point.measurement("player_online")
                .tag("name", name)
                .tag("server", serverName)
                .time(System.currentTimeMillis(), timeUnit)
                .addField("status", isOnline ? 1 :0)
                .build();

        batchPointsBuilder.point(point);

        influxDB.write(batchPointsBuilder.build());
    }

}
