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

    public AuthenticationHandler(DatabaseManager databaseManager) {
        this.state = AuthState.AWAITING_LOGIN_REGISTER;
        this.databaseManager = databaseManager;
    }

    public boolean handleInput(String input, ClientSession clientSession) throws IOException {
        switch (this.state) {
            case AWAITING_LOGIN_REGISTER:
                if (input != null && !input.trim().isEmpty()) {
                    String to_print = "\n-----------------------------------------------\n" +
                                        "|   Please enter your username. (q to quit)   |\n" +
                                        "-----------------------------------------------\n";

                    if(input.equals("0")){
                        this.state = AuthState.AWAITING_REGISTER_USERNAME;
                        clientSession.writer(to_print);
                    } 
                    else if(input.equals("1")){
                        this.state = AuthState.AWAITING_LOGIN_USERNAME;
                        clientSession.writer(to_print);
                    }
                    else{
                        clientSession.writer("\n-----------------------------------------------\n" +
                                         "|   Invalid input, please try again           |\n" +
                                         "-----------------------------------------------\n");
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
                        clientSession.writer("\n-----------------------------------------------\n" +
                                         "|   Please enter your password.               |\n" +
                                         "-----------------------------------------------\n");
                        
                    }
                    else {
                        clientSession.writer("\n-----------------------------------------------\n" +
                                         "|   Invalid username. Please try again.       |\n" +
                                         "-----------------------------------------------\n");
                        
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
                        clientSession.writer("\n-----------------------------------------------\n" +
                                         "| Username already exists. Please try again.  |\n" +
                                         "-----------------------------------------------\n");
                        
                    }
                    else {
                        this.state = AuthState.AWAITING_REGISTER_PASSWORD;
                        clientSession.writer("\n-----------------------------------------------\n" +
                                         "|   Please enter your password.               |\n" +
                                         "-----------------------------------------------\n");
                        
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
                        clientSession.writer("\n-----------------------------------------------\n" +
                                         "|         Authentication successful.          |\n" +
                                         "-----------------------------------------------\n\n" +
                                         "-----------------------------------------------\n" +
                                         "|        Welcome to the Waiting Room.         |\n" +
                                         "-----------------------------------------------\n" +
                                         "|  Please wait while we find another          |\n" +
                                         "|  player to join you.                        |\n" +
                                         "-----------------------------------------------\n\n");
                        
                        return true;
                    } else {
                        clientSession.writer("\n-----------------------------------------------\n" +
                                         "|   Invalid password. Please try again.       |\n" +
                                         "-----------------------------------------------\n");
                        
                        this.state = AuthState.AWAITING_LOGIN_PASSWORD;
                    }
                } else {
                    clientSession.writer("\n-----------------------------------------------\n" +
                                     "|   Invalid password. Please try again.       |\n" +
                                     "-----------------------------------------------\n\n");
                    
                }
                return false;

            case AWAITING_REGISTER_PASSWORD:
                if (input != null && !input.trim().isEmpty()) {
                    if(input.equals("q")){
                        this.state = AuthState.AWAITING_LOGIN_REGISTER;
                        return false;
                    }
                        clientSession.writer("\n-----------------------------------------------\n" +
                                        "|         Authentication successful.          |\n" +
                                        "-----------------------------------------------\n\n" +
                                        "-----------------------------------------------\n" +
                                        "|        Welcome to the Waiting Room.         |\n" +
                                        "-----------------------------------------------\n" +
                                        "|  Please wait while we find another          |\n" +
                                        "|  player to join you.                        |\n" +
                                        "-----------------------------------------------\n\n");
                        
                        if(databaseManager.register(this.username, input)){
                            return true;
                        }
                        else{
                            clientSession.writer("\n-----------------------------------------------\n" +
                                                "| Username already exists. Please try again.  |\n" +
                                                "-----------------------------------------------\n");
                            
                            this.state = AuthState.AWAITING_REGISTER_USERNAME;
                        }
                } else {
                    clientSession.writer("\n-----------------------------------------------\n" +
                                     "|   Invalid password. Please try again.       |\n" +
                                     "-----------------------------------------------\n");
                    
                }
                return false;
            default:
                return false;
        }
    }

    public String getUsername() {
        return username;
    }
}