package org.project.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchmakingPool implements Runnable {
    private Map<String, List<ClientSession>> availableClients;

    public MatchmakingPool() {
        this.availableClients = new HashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            for (String operatingMode : availableClients.keySet()) {
                List<ClientSession> clients = availableClients.get(operatingMode);
                if (clients.size() >= 2) {
                    ClientSession client1 = clients.removeFirst();
                    ClientSession client2 = clients.removeFirst();

                    client1.changeState(ClientStateEnum.IN_GAME);
                    client1.addedToMatchmakingPool = false;

                    client2.changeState(ClientStateEnum.IN_GAME);
                    client2.addedToMatchmakingPool = false;

                    UUID gameId = UUID.randomUUID();

                    Game game = new Game(gameId, client1, client2);
                    ClientSession.games.put(gameId, game);

                    client1.setGameId(gameId);
                    client2.setGameId(gameId);
                }
            }
        }
    }

    public void addClient(String operatingMode, ClientSession client) {
        availableClients.putIfAbsent(operatingMode, new ArrayList<>());
        availableClients.get(operatingMode).add(client);
    }


}