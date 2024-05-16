package org.project.server;

import org.project.database.DatabaseManager;

import java.time.LocalDateTime;

public class User {
    private String username;
    private int rank;
    private String token;
    private boolean isOnline = true;
    private LocalDateTime lastOnline;

    public User(String username, int rank, String token) {
        this.username = username;
        this.rank = rank;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isOnline() {
        return isOnline;
    }
    public LocalDateTime getLastOnline() {
        return lastOnline;
    }

    public void goOffline(DatabaseManager databaseManager) {
        isOnline = false;
        lastOnline = LocalDateTime.now();
        databaseManager.updateClient(username, rank, lastOnline);
    }
}