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
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│   Please enter your password.               │\n" +
                                         "└═════════════════════════════════════════════┘");
                    }
                    else {
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│   Invalid username. Please try again.       │\n" +
                                         "└═════════════════════════════════════════════┘");
                    }
                }
                return false;
            case AWAITING_PASSWORD:
                if (input != null && !input.trim().isEmpty()) {
                    if (databaseManager.verifyPassword(username, input)) {
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│         Authentication successful.          │\n" +
                                         "└═════════════════════════════════════════════┘\n\n" +
                                         "┌═════════════════════════════════════════════┐\n" +
                                         "│        Welcome to the Waiting Room.         │\n" +
                                         "├─────────────────────────────────────────────┤\n" +
                                         "│  Please wait while we find another          │\n" +
                                         "│  player to join you.                        │\n" +
                                         "└═════════════════════════════════════════════┘\n");
                        return true;
                    } else {
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│   Invalid password. Please try again.       │\n" +
                                         "└═════════════════════════════════════════════┘");
                        this.state = AuthState.AWAITING_PASSWORD;
                    }
                } else {
                    writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                     "│   Invalid password. Please try again.       │\n" +
                                     "└═════════════════════════════════════════════┘");
                }
                return false;
            default:
                return false;
        }
    }
}