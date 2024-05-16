package org.project.server;

import org.project.database.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.*;

public class ClientSession implements Runnable {
    private static ArrayList<ClientSession> clientSessions = new ArrayList<>();
    public static Map<UUID,Game> games = new HashMap<>();
    public Server server;
    public UUID gameId;
    private final SSLSocket clientSocket;
    private BufferedReader reader;
    public BufferedWriter writer;
    private ClientStateEnum state;
    private AuthenticationHandler authHandler;
    private final MatchmakingPool matchmakingPool;
    public boolean addedToMatchmakingPool = false;

    public ClientSession(SSLSocket clientSocket, MatchmakingPool matchmakingPool, Server server) {
        this.clientSocket = clientSocket;
        clientSessions.add(this);
        this.state = ClientStateEnum.INITIAL;
        this.matchmakingPool = matchmakingPool;
        this.server = server;
        this.authHandler = new AuthenticationHandler(server.databaseManager);
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

    private void handleInput(String input) throws IOException {
        //writer.println("State: " + state);
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
                    authHandler = new AuthenticationHandler(this.server.databaseManager);
                }
                if (authHandler.handleInput(input, writer)) {
                    this.state = ClientStateEnum.WAITING_ROOM;
                    authHandler = null;
                }
                break;
            case WAITING_ROOM:
                // Handle waiting room logic
                if(!addedToMatchmakingPool) {
                    matchmakingPool.addClient("Simple",this);
                    addedToMatchmakingPool = true;
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
                // Handle left game logic
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
}