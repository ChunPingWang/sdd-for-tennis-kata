package com.tennisscoring.domain.exception;

/**
 * Exception thrown when a requested player cannot be found in a match
 * 當在比賽中找不到請求的球員時拋出的異常
 */
public class PlayerNotFoundException extends RuntimeException {
    
    private final String playerId;
    private final String matchId;
    
    public PlayerNotFoundException(String playerId) {
        super("Player not found with ID: " + playerId);
        this.playerId = playerId;
        this.matchId = null;
    }
    
    public PlayerNotFoundException(String playerId, String matchId) {
        super("Player not found with ID: " + playerId + " in match: " + matchId);
        this.playerId = playerId;
        this.matchId = matchId;
    }
    
    public PlayerNotFoundException(String playerId, String matchId, Throwable cause) {
        super("Player not found with ID: " + playerId + " in match: " + matchId, cause);
        this.playerId = playerId;
        this.matchId = matchId;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public String getMatchId() {
        return matchId;
    }
}