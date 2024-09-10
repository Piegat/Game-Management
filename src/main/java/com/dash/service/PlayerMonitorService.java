package com.dash.service;

import com.dash.entity.Player;
import com.dash.entity.Server;
import com.dash.repository.PlayerRepository;
import com.dash.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class PlayerMonitorService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ServerRepository serverRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "https://servers-frontend.fivem.net/api/servers/single/";

    private final Map<String, LocalDateTime> playerStartTimes = new HashMap<>();
    private final Map<String, Boolean> playerStatus = new HashMap<>();

    @Scheduled(fixedRate = 15000)
    public void checkPlayerStatus() {
        List<Server> servers = serverRepository.findAll();

        for(Server server : servers) {
            try {
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

                    if (isPlayerOnline && (playerStatus.getOrDefault(playerLicense, false) == false)) {
                        playerStatus.put(playerLicense, true);
                        playerStartTimes.put(playerLicense, LocalDateTime.now());
                        System.out.println("O jogador " + dbPlayer.getName() + " com license " + playerLicense + " está online  no servidor:" + server.getName() + ". Início marcado.");
                    }
                    else if (isPlayerOnline && playerStatus.getOrDefault(playerLicense, false)) {
                        Duration elapsedTime = Duration.between(playerStartTimes.get(playerLicense), LocalDateTime.now());
                        System.out.println("O jogador " + dbPlayer.getName() + " com license " + playerLicense + " está online há " + elapsedTime.toMinutes() + " minutos no servidor: " + server.getName());
                        Long time_played;
                        if(dbPlayer.getMinutes_played() == null){
                            time_played = elapsedTime.toMinutes();
                        }else {
                            time_played = dbPlayer.getMinutes_played() + elapsedTime.toMinutes();
                        }
                        dbPlayer.setMinutes_played(time_played);
                        playerRepository.save(dbPlayer);
                    }
                    else if (!isPlayerOnline && playerStatus.getOrDefault(playerLicense, false)) {
                        playerStatus.put(playerLicense, false);
                        Duration elapsedTime = Duration.between(playerStartTimes.get(playerLicense), LocalDateTime.now());
                        System.out.println("O jogador " + dbPlayer.getName() + " com license " + playerLicense + " ficou offline. Estava online por " + elapsedTime.toMinutes() + " minutos  no servidor: " + server.getName());
                        playerStartTimes.remove(playerLicense);
                    } else {
                        System.out.println("O jogador " + dbPlayer.getName() + "  com license " + playerLicense + " está offline no servidor: " + server.getName());
                    }
                }

            } catch (Exception e) {
                System.out.println("Erro ao fazer requisição: " + e.getMessage());
            }
        }
    }
}
