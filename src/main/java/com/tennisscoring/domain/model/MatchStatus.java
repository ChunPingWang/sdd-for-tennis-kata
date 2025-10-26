package com.tennisscoring.domain.model;

/**
 * Enumeration representing the status of a tennis match.
 */
public enum MatchStatus {
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");
    
    private final String displayName;
    
    MatchStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Check if the match is still active.
     * @return true if match is in progress
     */
    public boolean isActive() {
        return this == IN_PROGRESS;
    }
    
    /**
     * Check if the match has ended.
     * @return true if match is completed or cancelled
     */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}