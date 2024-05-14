package server;

import java.io.IOException;
import java.io.PrintWriter;
import database.DatabaseManager;

public class AuthenticationHandler {
    private enum AuthState {
        AWAITING_USERNAME,
        AWAITING_PASSWORD
    }

    private AuthState state;
    private String username;
    private DatabaseManager databaseManager;

    public AuthenticationHandler() {
        this.state = AuthState.AWAITING_USERNAME;
        this.databaseManager = new DatabaseManager();
    }

    public boolean handleInput(String input, PrintWriter writer) throws IOException {
        switch (state) {
            case AWAITING_USERNAME:
                if (input != null && !input.trim().isEmpty()) {
                    this.username = input;
                    if(databaseManager.verifyUsername(username)){
                        this.state = AuthState.AWAITING_PASSWORD;
                        writer.println("Please enter your password.");
                    }
                    else {
                        writer.println("Invalid username. Please try again.");
                    }
                }
                return false;
            case AWAITING_PASSWORD:
                if (input != null && !input.trim().isEmpty()) {
                    if (databaseManager.verifyPassword(username, input)) {
                        writer.println("Authentication successful. Welcome to the waiting room.");
                        return true;
                    } else {
                        writer.println("Invalid password. Please try again.");
                        this.state = AuthState.AWAITING_PASSWORD;
                    }
                } else {
                    writer.println("Invalid password. Please try again.");
                }
                return false;
            default:
                return false;
        }
    }
}