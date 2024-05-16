package org.project.server;

public enum UserStateEnum {
    INITIAL,
    AUTHENTICATING,
    WAITING_ROOM,
    IN_GAME,
    GAME_OVER,
    OFFLINE
}
