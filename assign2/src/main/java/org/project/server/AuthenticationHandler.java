package org.project.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import org.project.database.DatabaseManager;

public class AuthenticationHandler {
    private enum AuthState {
        AWAITING_TOKEN,
        AWAITING_AUTH_TYPE,
        AWAITING_REGISTER_USERNAME,
        AWAITING_REGISTER_PASSWORD,
        AWAITING_LOGIN_USERNAME,
        AWAITING_LOGIN_PASSWORD
    }

    private AuthState state;
    private String username;
    private String token;
    private DatabaseManager databaseManager;

    public AuthenticationHandler(DatabaseManager databaseManager) {
        this.state = AuthState.AWAITING_TOKEN;
        this.databaseManager = databaseManager;
    }

    public boolean handleInput(String input, ClientSession clientSession) throws IOException {
        String[] inputParts = input.split(",");
        System.out.println("inputParts: " + Arrays.toString(inputParts));
        switch (this.state) {
            case AWAITING_TOKEN:
                if (inputParts[0].equals("TOKEN") && inputParts.length == 2) {
                    this.token = inputParts[1];
                    if (databaseManager.verifyToken(token, clientSession)) {
                        clientSession.write("AUTHENTICATED," + token + "\n");
                        successfulAuthentication(clientSession);
                        return true;
                    } else {
                        clientSession.write("REQUEST_AUTH_TYPE\n");
                        this.state = AuthState.AWAITING_AUTH_TYPE;
                    }
                }
                return false;

            case AWAITING_AUTH_TYPE:
                if (inputParts[0].equals("AUTH_TYPE") && inputParts.length == 2) {
                    if (inputParts[1].equals("REGISTER")) {
                        this.state = AuthState.AWAITING_REGISTER_USERNAME;
                        clientSession.write("REQUEST_USERNAME\n");
                    } else if (inputParts[1].equals("LOGIN")) {
                        this.state = AuthState.AWAITING_LOGIN_USERNAME;
                        clientSession.write("REQUEST_USERNAME\n");
                    } else {
                        clientSession.write("REQUEST_AUTH_TYPE\n");
                    }
                }
                return false;

            case AWAITING_LOGIN_USERNAME:
                if (inputParts[0].equals("USERNAME") && inputParts.length == 2) {
                    this.username = inputParts[1];
                    if (databaseManager.verifyUsername(username)) {
                        this.state = AuthState.AWAITING_LOGIN_PASSWORD;
                        clientSession.write("REQUEST_PASSWORD\n");
                    } else {
                        clientSession.write("REQUEST_USERNAME\n");
                    }
                }
                return false;

            case AWAITING_LOGIN_PASSWORD:
                if (inputParts[0].equals("PASSWORD") && inputParts.length == 2) {
                    String newToken = UUID.randomUUID().toString();
                    if (databaseManager.verifyPassword(this.username, inputParts[1], clientSession.getUser(), newToken)) {
                        this.token = newToken;
                        clientSession.write("AUTHENTICATED," + token + "\n");
                        successfulAuthentication(clientSession);
                        return true;
                    } else {
                        clientSession.write("REQUEST_PASSWORD\n");
                    }
                }
                return false;

            case AWAITING_REGISTER_USERNAME:
                if (inputParts[0].equals("USERNAME") && inputParts.length == 2) {
                    this.username = inputParts[1];
                    if (!databaseManager.verifyUsername(username)) {
                        this.state = AuthState.AWAITING_REGISTER_PASSWORD;
                        clientSession.write("REQUEST_PASSWORD\n");
                    } else {
                        clientSession.write("REQUEST_USERNAME\n");
                    }
                }
                return false;

            case AWAITING_REGISTER_PASSWORD:
                if (inputParts[0].equals("PASSWORD") && inputParts.length == 2) {
                    String newToken = UUID.randomUUID().toString();
                    if (databaseManager.register(this.username, inputParts[1], clientSession.getUser(), newToken)) {
                        this.token = newToken;
                        clientSession.write("AUTHENTICATED," + token + "\n");
                        successfulAuthentication(clientSession);
                        return true;
                    } else {
                        clientSession.write("REQUEST_USERNAME\n");
                    }
                }
                return false;

            default:
                return false;
        }
    }

    public void successfulAuthentication(ClientSession clientSession) {
        clientSession.write(
                        "\n-----------------------------------------------\n" +
                        "|         Authentication successful.          |\n" +
                        "-----------------------------------------------\n\n" +
                        "-----------------------------------------------\n" +
                        "|        Welcome to the Waiting Room.         |\n" +
                        "-----------------------------------------------\n" +
                        "|  Please wait while we find another          |\n" +
                        "|  player to join you.                        |\n" +
                        "-----------------------------------------------\n\n"
        );
    }

}