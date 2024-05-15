package org.project.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.project.database.DatabaseManager;

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

    public boolean handleInput(String input, BufferedWriter writer) throws IOException {
        switch (this.state) {
            case AWAITING_LOGIN_REGISTER:
                if (input != null && !input.trim().isEmpty()) {
                    String to_print = "\n-----------------------------------------------\n" +
                                        "|   Please enter your username. (q to quit)   |\n" +
                                        "-----------------------------------------------\n";

                    if(input.equals("0")){
                        this.state = AuthState.AWAITING_REGISTER_USERNAME;
                        writer.write(to_print);
                        writer.flush();
                    } 
                    else if(input.equals("1")){
                        this.state = AuthState.AWAITING_LOGIN_USERNAME;
                        writer.write(to_print);
                        writer.flush();
                    }
                    else{
                        writer.write("\n-----------------------------------------------\n" +
                                         "|   Invalid input, please try again           |\n" +
                                         "-----------------------------------------------\n");
                        writer.flush();
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
                        writer.write("\n-----------------------------------------------\n" +
                                         "|   Please enter your password.               |\n" +
                                         "-----------------------------------------------\n");
                        writer.flush();
                    }
                    else {
                        writer.write("\n-----------------------------------------------\n" +
                                         "|   Invalid username. Please try again.       |\n" +
                                         "-----------------------------------------------\n");
                        writer.flush();
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
                    if(databaseManager.usernameExists(username)){
                        writer.write("\n-----------------------------------------------\n" +
                                         "| Username already exists. Please try again.  |\n" +
                                         "-----------------------------------------------\n");
                        writer.flush();
                    }
                    else {
                        this.state = AuthState.AWAITING_REGISTER_PASSWORD;
                        writer.write("\n-----------------------------------------------\n" +
                                         "|   Please enter your password.               |\n" +
                                         "-----------------------------------------------\n");
                        writer.flush();
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
                        writer.write("\n-----------------------------------------------\n" +
                                         "|         Authentication successful.          |\n" +
                                         "-----------------------------------------------\n\n" +
                                         "-----------------------------------------------\n" +
                                         "|        Welcome to the Waiting Room.         |\n" +
                                         "-----------------------------------------------\n" +
                                         "|  Please wait while we find another          |\n" +
                                         "|  player to join you.                        |\n" +
                                         "-----------------------------------------------\n\n");
                        writer.flush();
                        return true;
                    } else {
                        writer.write("\n-----------------------------------------------\n" +
                                         "|   Invalid password. Please try again.       |\n" +
                                         "-----------------------------------------------\n");
                        writer.flush();
                        this.state = AuthState.AWAITING_LOGIN_PASSWORD;
                    }
                } else {
                    writer.write("\n-----------------------------------------------\n" +
                                     "|   Invalid password. Please try again.       |\n" +
                                     "-----------------------------------------------\n\n");
                    writer.flush();
                }
                return false;

            case AWAITING_REGISTER_PASSWORD:
                if (input != null && !input.trim().isEmpty()) {
                    if(input.equals("q")){
                        this.state = AuthState.AWAITING_LOGIN_REGISTER;
                        return false;
                    }
                    if(true){ // for password validity checking (if implemented)
                        writer.write("\n-----------------------------------------------\n" +
                                        "|         Authentication successful.          |\n" +
                                        "-----------------------------------------------\n\n" +
                                        "-----------------------------------------------\n" +
                                        "|        Welcome to the Waiting Room.         |\n" +
                                        "-----------------------------------------------\n" +
                                        "|  Please wait while we find another          |\n" +
                                        "|  player to join you.                        |\n" +
                                        "-----------------------------------------------\n\n");
                        writer.flush();
                        databaseManager.register(this.username, input);
                        return true;
                    } else {
                        writer.write("\n-----------------------------------------------\n" +
                                         "|   Invalid password. Please try again.       |\n" +
                                         "-----------------------------------------------\n");
                        writer.flush();
                        this.state = AuthState.AWAITING_REGISTER_PASSWORD;
                    }
                } else {
                    writer.write("\n-----------------------------------------------\n" +
                                     "|   Invalid password. Please try again.       |\n" +
                                     "-----------------------------------------------\n");
                    writer.flush();
                }
                return false;
            default:
                return false;
        }
    }
}