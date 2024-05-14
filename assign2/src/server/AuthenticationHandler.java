package server;

import java.io.PrintWriter;

public class AuthenticationHandler {
    private enum AuthState {
        AWAITING_USERNAME,
        AWAITING_PASSWORD
    }

    private AuthState state;
    private String username;

    public AuthenticationHandler() {
        this.state = AuthState.AWAITING_USERNAME;
    }

    public boolean handleInput(String input, PrintWriter writer) {
        switch (state) {
            case AWAITING_USERNAME:
                writer.println("Please enter your username.");
                if (input != null && !input.trim().isEmpty()) {
                    this.username = input;
                    this.state = AuthState.AWAITING_PASSWORD;
                } else {
                    writer.println("Invalid username. Please try again.");
                }
                return false;
            case AWAITING_PASSWORD:
                writer.println("Please enter your password.");
                if (input != null && !input.trim().isEmpty()) {
                    writer.println("Authentication successful. Welcome to the waiting room.");
                    return true;
                } else {
                    writer.println("Invalid password. Please try again.");
                }
                return false;
            default:
                return false;
        }
    }
}