package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientSession implements Runnable {
    private static ArrayList<ClientSession> clientSessions = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ClientStateEnum state;
    private AuthenticationHandler authHandler;
    private MatchmakingPool matchmakingPool;
    private boolean addedToMatchmakingPool = false;

    public ClientSession(Socket clientSocket, MatchmakingPool matchmakingPool) {
        this.clientSocket = clientSocket;
        clientSessions.add(this);
        this.state = ClientStateEnum.AUTHENTICATING;
        this.matchmakingPool = matchmakingPool;

        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println("Welcome to the server.");
        }  catch (IOException e) {
            System.out.println("Exception creating reader and writer: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);

                handleInput(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Exception closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleInput(String input) {
        writer.println("State: " + state);
        switch (state) {
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
                writer.println("Game logic not implemented yet.");
                // Handle in-game logic
                break;
            case GAME_OVER:
                writer.println("Game over. Thanks for playing!");
                // Handle left game logic
                break;
            default:
                break;
        }
    }

    public void changeState(ClientStateEnum newState) {
        this.state = newState;
    }
}