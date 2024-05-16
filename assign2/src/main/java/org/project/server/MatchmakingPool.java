package org.project.server;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MatchmakingPool implements Runnable {
    private final List<ClientSession> simplePlayers;
    private final Object lock = new Object();

    public MatchmakingPool() {
        this.simplePlayers = new ArrayList<>();
    }

    private void checkAndRemoveOfflineClients() {
        synchronized (lock) {
            Iterator<ClientSession> iterator = simplePlayers.iterator();
            while (iterator.hasNext()) {
                ClientSession client = iterator.next();
                if (!client.isOnline() && Duration.between(client.getLastOnline(), LocalDateTime.now()).toSeconds() >= 60) {
                    iterator.remove();
                    System.out.println("Client " + client.getUsername() + " removed from matchmaking pool due to inactivity.");
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            checkAndRemoveOfflineClients();
            synchronized (lock) {
                if (simplePlayers.size() >= 2) {
                    ClientSession client1 = null;
                    ClientSession client2 = null;

                    for (ClientSession client : simplePlayers) {
                        if (client.isOnline()) {
                            if (client1 == null) {
                                client1 = client;
                            } else if (client2 == null) {
                                client2 = client;
                            }

                            if (client1 != null && client2 != null) {
                                break;
                            }
                        }
                    }

                    if (client1 != null && client2 != null) {
                        simplePlayers.remove(client1);
                        simplePlayers.remove(client2);

                        client1.changeState(ClientStateEnum.IN_GAME);
                        client2.changeState(ClientStateEnum.IN_GAME);

                        UUID gameId = UUID.randomUUID();

                        Game game = null;
                        try {
                            game = new Game(gameId, client1, client2);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        synchronized (ClientSession.games) {
                            ClientSession.games.put(gameId, game);
                        }

                        client1.setGameId(gameId);
                        client2.setGameId(gameId);
                    }
                }
            }
        }
    }
    public void addClient(ClientSession client) {
        synchronized (lock) {
            simplePlayers.add(client);
        }
    }
}