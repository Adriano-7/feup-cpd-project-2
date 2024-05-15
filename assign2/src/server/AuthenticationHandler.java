package server;

import java.io.IOException;
import java.io.PrintWriter;
import database.DatabaseManager;

public class AuthenticationHandler {
    private enum AuthState {
        AWAITING_LOGIN_REGISTER,
        AWAITING_LOGIN_USERNAME,
        AWAITING_REGISTER_USERNAME,
        AWAITING_LOGIN_PASSWORD,
        AWAITING_REGISTER_PASSWORD
    }

    private AuthState state;
    private String username;
    private DatabaseManager databaseManager;

    public AuthenticationHandler() {
        this.state = AuthState.AWAITING_LOGIN_REGISTER;
        this.databaseManager = new DatabaseManager();
    }

    public boolean handleInput(String input, PrintWriter writer) throws IOException {
        switch (this.state) {
            case AWAITING_LOGIN_REGISTER:
                if (input != null && !input.trim().isEmpty()) {
                    String to_print = "\n┌═════════════════════════════════════════════┐\n" +
                                        "│   Please enter your username. (q to quit)   │\n" +
                                        "└═════════════════════════════════════════════┘";

                    if(input.equals("0")){
                        this.state = AuthState.AWAITING_REGISTER_USERNAME;
                        writer.println(to_print);
                    } 
                    if(input.equals("1")){
                        this.state = AuthState.AWAITING_LOGIN_USERNAME;
                        writer.println(to_print);
                    }
                    else{
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│   Invalid input, please try again           │\n" +
                                         "└═════════════════════════════════════════════┘"); 
                    }
                }
                return false;
            case AWAITING_LOGIN_USERNAME:
                if (input != null && !input.trim().isEmpty()) {
                    this.username = input;
                    if(input.equals("q")){
                        this.state = AuthState.AWAITING_LOGIN_REGISTER;
                        return false;
                    }
                    if(databaseManager.verifyUsername(username)){
                        this.state = AuthState.AWAITING_LOGIN_PASSWORD;
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
            case AWAITING_REGISTER_USERNAME:
                if (input != null && !input.trim().isEmpty()) {
                    this.username = input;
                    if(input.equals("q")){
                        this.state = AuthState.AWAITING_LOGIN_REGISTER;
                        return false;
                    }
                    if(!databaseManager.verifyUsername(username)){
                        this.state = AuthState.AWAITING_REGISTER_PASSWORD;
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│   Please enter your password.               │\n" +
                                         "└═════════════════════════════════════════════┘");
                    }
                    else {
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│   Username already exists. Please try again.│\n" +
                                         "└═════════════════════════════════════════════┘");
                    }
                }
                return false;
            case AWAITING_LOGIN_PASSWORD:
                if (input != null && !input.trim().isEmpty()) {
                    if(input.equals("q")){
                        this.state = AuthState.AWAITING_LOGIN_REGISTER;
                        return false;
                    }
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
                        this.state = AuthState.AWAITING_LOGIN_PASSWORD;
                    }
                } else {
                    writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                     "│   Invalid password. Please try again.       │\n" +
                                     "└═════════════════════════════════════════════┘");
                }
                return false;
            case AWAITING_REGISTER_PASSWORD:
                if (input != null && !input.trim().isEmpty()) {
                    if(input.equals("q")){
                        this.state = AuthState.AWAITING_LOGIN_REGISTER;
                        return false;
                    }
                    if(true){ // for password validity checking (if implemented)
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                        "│         Authentication successful.          │\n" +
                                        "└═════════════════════════════════════════════┘\n\n" +
                                        "┌═════════════════════════════════════════════┐\n" +
                                        "│        Welcome to the Waiting Room.         │\n" +
                                        "├─────────────────────────────────────────────┤\n" +
                                        "│  Please wait while we find another          │\n" +
                                        "│  player to join you.                        │\n" +
                                        "└═════════════════════════════════════════════┘\n");
                        databaseManager.register(this.username, input);
                        return true;
                    } else {
                        writer.println("\n┌═════════════════════════════════════════════┐\n" +
                                         "│   Invalid password. Please try again.       │\n" +
                                         "└═════════════════════════════════════════════┘");
                        this.state = AuthState.AWAITING_REGISTER_PASSWORD;
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