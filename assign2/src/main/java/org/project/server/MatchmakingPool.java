package org.project.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.TreeSet;
import org.project.database.DatabaseManager;

public class MatchmakingPool implements Runnable {
    private Map<String, List<ClientSession>> availableClients;

    private DatabaseManager databaseManager;
    public MatchmakingPool() {
        this.availableClients = new HashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            for (String operatingMode : availableClients.keySet()) {
                List<ClientSession> clients = availableClients.get(operatingMode);
                if (clients.size() >= 2) {
                    if (operatingMode.equals("simple")) {
                        ClientSession client1 = clients.removeFirst();
                        ClientSession client2 = clients.removeFirst();

                        client1.changeState(ClientStateEnum.IN_GAME);
                        client1.updateMatchmakingStatus(false);

                        client2.changeState(ClientStateEnum.IN_GAME);
                        client2.updateMatchmakingStatus(false);

                        UUID gameId = UUID.randomUUID();

                        Game game = null;
                        try {
                            game = new Game(gameId, client1, client2);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ClientSession.games.put(gameId, game);

                        client1.setGameId(gameId);
                        client2.setGameId(gameId);

                    } else if (operatingMode.equals("ranked")) {
                        clients.sort((c1, c2) -> {
                            try {
                                return databaseManager.getRank(c1.getUsername()) -
                                        databaseManager.getRank(c2.getUsername());
                            } catch (IOException e) {
                                System.out.println("Error fetching Elo ratings: " + e.getMessage());
                                e.printStackTrace();
                                return 0; // or handle the error in a different way
                            }
                        });

                        if (clients.size() >= 2) {
                            ClientSession client1 = clients.removeFirst();
                            ClientSession client2 = clients.removeFirst();

                            client1.changeState(ClientStateEnum.IN_GAME);
                            client1.updateMatchmakingStatus(false);

                            client2.changeState(ClientStateEnum.IN_GAME);
                            client2.updateMatchmakingStatus(false);

                            UUID gameId = UUID.randomUUID();

                            Game game = null;
                            try {
                                game = new Game(gameId, client1, client2);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            ClientSession.games.put(gameId, game);

                            client1.setGameId(gameId);
                            client2.setGameId(gameId);
                        }
                    }
                }
            }
        }
    }

    public void addClient(String operatingMode, ClientSession client) {
        availableClients.putIfAbsent(operatingMode, new ArrayList<>());
        availableClients.get(operatingMode).add(client);
    }
}