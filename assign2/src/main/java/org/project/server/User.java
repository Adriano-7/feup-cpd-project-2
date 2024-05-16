package org.project.server;

import org.project.database.DatabaseManager;
import java.time.LocalDateTime;

public class User {
    private ClientStateEnum state;
    private String username;
    private Integer rank;
    private String token;
    private boolean isOnline;
    private LocalDateTime lastOnline;

    public User() {
        this.username = null;
        this.rank = null;
        this.token = null;
        this.state = ClientStateEnum.INITIAL;
        this.isOnline = false;
        this.lastOnline = null;
    }

    public void populate(String username, int rank, String token, boolean isOnline, LocalDateTime lastOnline) {
        this.username = username;
        this.rank = rank;
        this.token = token;
        this.isOnline = isOnline;
        this.lastOnline = lastOnline;
    }

    public String getUsername() {
        return username;
    }
    public int getRank() {
        return rank;
    }
    public String getToken() {
        return token;
    }
    public boolean isOnline() {return isOnline;}

    public void setUsername(String username) {
        this.username = username;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public LocalDateTime getLastOnline() {return lastOnline;}


    public void goOffline(DatabaseManager databaseManager) {
        isOnline = false;
        lastOnline = LocalDateTime.now();
        databaseManager.updateClient(username, rank, lastOnline);
    }
}