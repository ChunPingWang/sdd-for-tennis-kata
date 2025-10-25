package com.tennisscoring.domain.model;

/**
 * Value object representing a player's name.
 * Immutable and validates input to ensure data integrity.
 */
public record PlayerName(String value) {
    
    public PlayerName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Player name cannot exceed 50 characters");
        }
        if (value.trim().length() < 2) {
            throw new IllegalArgumentException("Player name must be at least 2 characters long");
        }
    }
    
    /**
     * Factory method to create PlayerName from string value.
     * @param name the player's name
     * @return a new PlayerName instance
     */
    public static PlayerName of(String name) {
        return new PlayerName(name);
    }
    
    /**
     * Get the string value of this PlayerName.
     * @return the trimmed name value
     */
    public String getValue() {
        return value.trim();
    }
    
    /**
     * Get the display format of the player name.
     * @return the formatted name for display purposes
     */
    public String getDisplayName() {
        return getValue();
    }
}