package org.project.server;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MatchmakingPool implements Runnable {
    private final List<User> simplePlayers;
    private final Object lock = new Object();

    public MatchmakingPool() {
        this.simplePlayers = new ArrayList<>();
    }

    private void checkAndRemoveOfflineClients() {
        synchronized (lock) {
            Iterator<User> iterator = simplePlayers.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (!user.isOnline() &&
                    Duration.between(user.getLastOnline(), LocalDateTime.now()).toSeconds() >= 60
                ) {
                    iterator.remove();
                    System.out.println("Client " + user.getUsername() + " removed from matchmaking pool due to inactivity.");
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
                    User user1 = null;
                    User user2 = null;

                    for (User user : simplePlayers) {
                        if (user.isOnline()) {
                            if (user1 == null) {
                                user1 = user;
                            } else if (user2 == null) {
                                user2 = user;
                            }

                            if (user1 != null && user2 != null) {
                                break;
                            }
                        }
                    }

                    if (user1 != null && user2 != null) {
                        simplePlayers.remove(user1);
                        simplePlayers.remove(user2);

                        user1.setState(UserStateEnum.IN_GAME);
                        user2.setState(UserStateEnum.IN_GAME);

                        UUID gameId = UUID.randomUUID();

                        Game game = null;
                        try {
                            game = new Game(gameId, user1.getClientSession(), user2.getClientSession());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        synchronized (ClientSession.games) {
                            ClientSession.games.put(gameId, game);
                        }

                        user1.getClientSession().setGameId(gameId);
                        user2.getClientSession().setGameId(gameId);
                    }
                }
            }
        }
    }
    public void addClient(User user) {
        synchronized (lock) {
            simplePlayers.add(user);
        }
    }
}