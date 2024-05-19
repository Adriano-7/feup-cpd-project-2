package org.project.server;

import org.project.database.DatabaseManager;
import java.time.LocalDateTime;


public class User {
    private UserStateEnum state;
    private String username;
    private Integer rank;
    private String token;
    private LocalDateTime lastOnline;
    private ClientSession clientSession;

    public User(ClientSession clientSession) {
        this.username = null;
        this.rank = null;
        this.token = null;
        this.state = UserStateEnum.AUTHENTICATING;
        this.lastOnline = null;
        this.clientSession = clientSession;
    }

    public void populate(String username, int rank, String token, LocalDateTime lastOnline) {
        this.username = username;
        this.rank = rank;
        this.token = token;
        this.lastOnline = lastOnline;
    }

    public String getUsername() {
        return username;
    }
    public LocalDateTime getLastOnline() {return lastOnline;}
    public UserStateEnum getState() {return state;}
    public void setState(UserStateEnum state) {this.state = state;}

    public boolean isOnline() {
        return state != UserStateEnum.OFFLINE;
    }

    public void goOffline(DatabaseManager databaseManager) {
        state = UserStateEnum.OFFLINE;
        lastOnline = LocalDateTime.now();
        databaseManager.updateClient(username, rank, lastOnline);
        AuthenticationHandler.removeAuthenticatedUser(username);
    }
    public ClientSession getClientSession() {
        return clientSession;
    }
}