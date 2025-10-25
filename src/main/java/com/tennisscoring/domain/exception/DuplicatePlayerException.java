package com.tennisscoring.domain.exception;

/**
 * Exception thrown when attempting to create a match with duplicate player names
 * 當嘗試使用重複球員姓名創建比賽時拋出的異常
 */
public class DuplicatePlayerException extends ValidationException {
    
    private final String playerName;
    
    public DuplicatePlayerException(String playerName) {
        super("playerName", playerName, "Player names must be different");
        this.playerName = playerName;
    }
    
    public DuplicatePlayerException(String playerName, Throwable cause) {
        super("playerName", playerName, "Player names must be different", cause);
        this.playerName = playerName;
    }
    
    public String getPlayerName() {
        return playerName;
    }
}