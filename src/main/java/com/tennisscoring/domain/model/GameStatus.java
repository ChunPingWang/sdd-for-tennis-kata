package com.tennisscoring.domain.model;

/**
 * Enumeration representing the status of a tennis game.
 */
public enum GameStatus {
    IN_PROGRESS("進行中"),
    DEUCE("平分"),
    ADVANTAGE_PLAYER1("球員1優勢"),
    ADVANTAGE_PLAYER2("球員2優勢"),
    COMPLETED("已完成");
    
    private final String displayName;
    
    GameStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Check if the game is still active (not completed).
     * @return true if game is in progress or in deuce/advantage state
     */
    public boolean isActive() {
        return this != COMPLETED;
    }
    
    /**
     * Check if the game is in a deuce situation.
     * @return true if game is in deuce or advantage state
     */
    public boolean isDeuceOrAdvantage() {
        return this == DEUCE || this == ADVANTAGE_PLAYER1 || this == ADVANTAGE_PLAYER2;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}