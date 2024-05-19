package org.project.server;

import org.project.database.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.util.*;
import javax.net.ssl.*;

public class ClientSession implements Runnable {
    private static ArrayList<ClientSession> clientSessions = new ArrayList<>();
    public static Map<UUID,Game> games = new HashMap<>();
    private Server server;
    private UUID gameId;
    private final SSLSocket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ClientStateEnum state;
    private AuthenticationHandler authHandler;
    private final MatchmakingPool matchmakingPool;
    private boolean addedToMatchmakingPool = false;
    private String username = null;
    private Integer rank = null;
    boolean isRanked = false;

    public ClientSession(SSLSocket clientSocket, MatchmakingPool matchmakingPool, Server server) {
        this.clientSocket = clientSocket;
        clientSessions.add(this);
        this.state = ClientStateEnum.INITIAL;
        this.matchmakingPool = matchmakingPool;
        this.server = server;
        this.authHandler = server.getAuthHandler();
        this.gameId = null;

        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            writer.write("\n-----------------------------------------------\n" +
                             "|            Welcome to the Server!           |\n" +
                             "-----------------------------------------------\n\n");
            writer.flush();

            handleInput("");
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
        if(state != ClientStateEnum.INITIAL && state != ClientStateEnum.AUTHENTICATING){
            //TODO: Update timestamp and set the client variable isOnline to false
        }
    }

    public String getUsername() {
        return this.username;
    }

    private void handleInput(String input) throws IOException {
        switch (state) {
            case INITIAL:
                //TODO: Handle token
                writer.write(
                            "\n-----------------------------------------------\n" +
                                "|              Select an option:              |\n" +
                                "|---------------------------------------------|\n" +
                                "|   Register                             [0]  |\n" +
                                "|   Login                                [1]  |\n" +
                                "-----------------------------------------------\n");
                writer.flush();

                this.state = ClientStateEnum.AUTHENTICATING;
                break;
            case AUTHENTICATING:
                if (authHandler == null) {
                    authHandler = server.getAuthHandler();
                }
                if (authHandler.handleInput(input, this)) {
                    this.state = ClientStateEnum.WAITING_ROOM;
                    authHandler = null;
                }
                break;
            case WAITING_ROOM:
                writer.write(
                        "\n-----------------------------------------------\n" +
                        "|              Select an option:              |\n" +
                        "|---------------------------------------------|\n" +
                        "|   Play Simple Game                     [0]  |\n" +
                        "|   Play Ranked Game                     [1]  |\n" +
                        "-----------------------------------------------\n");
                if(!addedToMatchmakingPool) {
                    if(Objects.equals(input, "0")) {
                        matchmakingPool.addClient("Simple",this);
                        addedToMatchmakingPool = true;
                    } else if(Objects.equals(input, "1")) {
                        isRanked = true;
                        matchmakingPool.addClient("Ranked",this);
                        addedToMatchmakingPool = true;
                    }
                }
                break;
            case IN_GAME:
                if(gameId != null) {
                    games.get(gameId).update(this, input);
                }
                break;
            case GAME_OVER:
                writer.write(
                        "-----------------------------------------------\n" +
                        "|                  GAME OVER                  |\n" +
                        "|---------------------------------------------|\n" +
                        "|  Thanks for playing!                        |\n" +
                        "-----------------------------------------------\n");
                writer.flush();

                state = ClientStateEnum.WAITING_ROOM;
                if(isRanked) {
                    // TODO: Update rank
                }
                break;
            default:
                break;
        }
    }

    public void changeState(ClientStateEnum newState) {
        this.state = newState;
    }
    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
    public void writer(String message) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }

    public void updateMatchmakingStatus(boolean isInMatchmakingPool) {
        this.addedToMatchmakingPool = isInMatchmakingPool;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setRank(Integer rank) {
        this.rank = rank;
    }
}