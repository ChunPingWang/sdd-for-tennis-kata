package com.tennisscoring.domain.model;

import java.util.UUID;

/**
 * Value object representing a unique match identifier.
 * Immutable and validates input to ensure data integrity.
 */
public record MatchId(String value) {
    
    public MatchId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
        // Validate UUID format
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Match ID must be a valid UUID format", e);
        }
    }
    
    /**
     * Factory method to generate a new unique MatchId.
     * @return a new MatchId with a randomly generated UUID
     */
    public static MatchId generate() {
        return new MatchId(UUID.randomUUID().toString());
    }
    
    /**
     * Factory method to create MatchId from string value.
     * @param value the string representation of the match ID
     * @return a new MatchId instance
     */
    public static MatchId of(String value) {
        return new MatchId(value);
    }
    
    /**
     * Get the string value of this MatchId.
     * @return the UUID string value
     */
    public String getValue() {
        return value;
    }
}