package com.tennisscoring.domain.model;

/**
 * Enumeration representing tennis game scores.
 * Includes both regular scoring (0, 15, 30, 40) and special states (Advantage).
 */
public enum GameScore {
    LOVE(0, "0"),
    FIFTEEN(15, "15"), 
    THIRTY(30, "30"), 
    FORTY(40, "40"), 
    ADVANTAGE(50, "AD");
    
    private final int numericValue;
    private final String displayValue;
    
    GameScore(int numericValue, String displayValue) {
        this.numericValue = numericValue;
        this.displayValue = displayValue;
    }
    
    /**
     * Get the next score after winning a point.
     * @return the next GameScore
     * @throws IllegalStateException if called on ADVANTAGE
     */
    public GameScore next() {
        return switch (this) {
            case LOVE -> FIFTEEN;
            case FIFTEEN -> THIRTY;
            case THIRTY -> FORTY;
            case FORTY -> throw new IllegalStateException("Cannot advance from FORTY without context");
            case ADVANTAGE -> throw new IllegalStateException("Cannot advance from ADVANTAGE");
        };
    }
    
    /**
     * Check if this score can win the game against opponent score.
     * @param opponentScore the opponent's current score
     * @return true if this score wins the game
     */
    public boolean winsAgainst(GameScore opponentScore) {
        if (this == ADVANTAGE) {
            return true;
        }
        if (this == FORTY) {
            return opponentScore != FORTY && opponentScore != ADVANTAGE;
        }
        return false;
    }
    
    /**
     * Check if this score creates a deuce situation.
     * @param opponentScore the opponent's current score
     * @return true if both scores are FORTY
     */
    public boolean isDeuce(GameScore opponentScore) {
        return this == FORTY && opponentScore == FORTY;
    }
    
    public int getNumericValue() {
        return numericValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    @Override
    public String toString() {
        return displayValue;
    }
}