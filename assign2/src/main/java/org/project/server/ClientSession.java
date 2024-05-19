package org.project.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.*;

public class ClientSession implements Runnable {
    private static ArrayList<User> users = new ArrayList<>();
    public static Map<UUID,Game> games = new HashMap<>();
    private Server server;
    private UUID gameId;
    private final SSLSocket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private AuthenticationHandler authHandler;
    private final MatchmakingPool matchmakingPool;
    private User user;

    public ClientSession(SSLSocket clientSocket, MatchmakingPool matchmakingPool, Server server) {
        this.clientSocket = clientSocket;
        this.user = new User(this);
        users.add(user);
        this.matchmakingPool = matchmakingPool;
        this.server = server;
        this.authHandler = null;
        this.gameId = null;

        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        }  catch (IOException e) {
            System.out.println("Exception creating reader and writer: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String input;
        while (clientSocket.isConnected()) {
            try {
                input = reader.readLine();
                handleInput(input);
            } catch (IOException e) {
                System.out.println("Exception handling client request: " + e.getMessage());
                break;
            }
        }
        if(user.getState() != UserStateEnum.AUTHENTICATING){
            user.goOffline(server.getDatabaseManager());
        }
    }

    private void handleInput(String input) throws IOException {
        switch (user.getState()) {
            case AUTHENTICATING:
                if (authHandler == null) {
                    authHandler = new AuthenticationHandler(server.getDatabaseManager());
                }
                if (authHandler.handleInput(input, this)) {
                    write(
                            "\n-----------------------------------------------\n" +
                            "|              Select an option:              |\n" +
                            "|---------------------------------------------|\n" +
                            "|   Play Simple Game                     [0]  |\n" +
                            "|   Play Ranked Game                     [1]  |\n" +
                            "-----------------------------------------------\n");
                    user.setState(UserStateEnum.CHOOSE_MATCH_TYPE);
                    authHandler = null;
                }
                break;
                case CHOOSE_MATCH_TYPE:
                    String welcomeMessage = "-----------------------------------------------\n" +
                                            "|        Welcome to the Waiting Room.         |\n" +
                                            "-----------------------------------------------\n" +
                                            "|  Please wait while we find another          |\n" +
                                            "|  player to join you.                        |\n" +
                                            "-----------------------------------------------\n\n";

                    if (input.equals("0")) {
                        user.setState(UserStateEnum.WAITING_ROOM);
                        MatchmakingPool.addClient(this.user, "Simple");
                        write(welcomeMessage);
                    } else if (input.equals("1")) {
                        user.setState(UserStateEnum.WAITING_ROOM);
                        MatchmakingPool.addClient(this.user, "Ranked");
                        write(welcomeMessage);
                    } else {
                        write("Invalid input. Please enter 1 or 2.\n");
                    }
            case WAITING_ROOM:
                break;
            case IN_GAME:
                if(gameId != null) {
                    games.get(gameId).update(this, input);
                }
                break;
            case GAME_OVER:
                write(
                        "\n-----------------------------------------------\n" +
                        "|              Select an option:              |\n" +
                        "|---------------------------------------------|\n" +
                        "|   Play Simple Game                     [0]  |\n" +
                        "|   Play Ranked Game                     [1]  |\n" +
                        "-----------------------------------------------\n");
                user.setState(UserStateEnum.CHOOSE_MATCH_TYPE);
                break;
            default:
                break;
        }
    }
    public void changeState(UserStateEnum newState) {
        user.setState(newState);
    }
    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
    public void write(String message) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}