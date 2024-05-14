package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientSession implements Runnable {
    private static ArrayList<ClientSession> clientSessions = new ArrayList<>();
    public static Map<UUID,Game> games = new HashMap<>();
    public UUID gameId;
    private final Socket clientSocket;
    private BufferedReader reader;
    public PrintWriter writer;
    private ClientStateEnum state;
    private AuthenticationHandler authHandler;
    private final MatchmakingPool matchmakingPool;
    public boolean addedToMatchmakingPool = false;

    public ClientSession(Socket clientSocket, MatchmakingPool matchmakingPool) {
        this.clientSocket = clientSocket;
        clientSessions.add(this);
        this.state = ClientStateEnum.INITIAL;
        this.matchmakingPool = matchmakingPool;
        this.authHandler = new AuthenticationHandler();
        this.gameId = null;

        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println("-------------------------\nWelcome to the server.\n-------------------------\n");
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
    }

    private void handleInput(String input) throws IOException {
        //writer.println("State: " + state);
        switch (state) {
            case INITIAL:
                //TODO: Handle token
                writer.println("Please enter your username.");
                this.state = ClientStateEnum.AUTHENTICATING;
                break;
            case AUTHENTICATING:
                if (authHandler == null) {
                    authHandler = new AuthenticationHandler();
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
                writer.println("Game over. Thanks for playing!");
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