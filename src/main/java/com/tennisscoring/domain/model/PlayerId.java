package com.tennisscoring.domain.model;

import java.util.UUID;

/**
 * Value object representing a unique player identifier.
 * Immutable and validates input to ensure data integrity.
 */
public record PlayerId(String value) {
    
    public PlayerId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
        // Validate UUID format
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Player ID must be a valid UUID format", e);
        }
    }
    
    /**
     * Factory method to generate a new unique PlayerId.
     * @return a new PlayerId with a randomly generated UUID
     */
    public static PlayerId generate() {
        return new PlayerId(UUID.randomUUID().toString());
    }
    
    /**
     * Factory method to create PlayerId from string value.
     * @param value the string representation of the player ID
     * @return a new PlayerId instance
     */
    public static PlayerId of(String value) {
        return new PlayerId(value);
    }
    
    /**
     * Get the string value of this PlayerId.
     * @return the UUID string value
     */
    public String getValue() {
        return value;
    }
}