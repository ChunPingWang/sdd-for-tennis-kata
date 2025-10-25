package com.tennisscoring.domain.exception;

/**
 * Exception thrown when a requested match cannot be found
 * 當找不到請求的比賽時拋出的異常
 */
public class MatchNotFoundException extends RuntimeException {
    
    public MatchNotFoundException(String matchId) {
        super("Match not found with ID: " + matchId);
    }
    
    public MatchNotFoundException(String matchId, Throwable cause) {
        super("Match not found with ID: " + matchId, cause);
    }
}