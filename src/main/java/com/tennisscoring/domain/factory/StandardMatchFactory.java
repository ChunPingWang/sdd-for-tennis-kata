package com.tennisscoring.domain.factory;

import com.tennisscoring.domain.model.Match;
import org.springframework.stereotype.Component;

/**
 * Factory for creating standard tennis matches (best of 3 sets).
 * 創建標準網球比賽的工廠 (三盤兩勝制)
 * 
 * This factory creates matches following standard tennis rules
 * with regular games and tiebreaks at 6-6.
 * 
 * Requirements: 2.1, 2.3, 5.1, 5.2
 */
@Component
public class StandardMatchFactory implements MatchFactory {
    
    private static final String MATCH_TYPE = "STANDARD";
    
    @Override
    public Match createMatch(String player1Name, String player2Name) {
        // Create a standard match using the existing factory method
        // This maintains backward compatibility while adding extensibility
        return Match.create(player1Name, player2Name);
    }
    
    @Override
    public boolean supports(String matchType) {
        return MATCH_TYPE.equalsIgnoreCase(matchType) || 
               "BEST_OF_3".equalsIgnoreCase(matchType) ||
               matchType == null; // Default to standard if no type specified
    }
    
    @Override
    public String getMatchType() {
        return MATCH_TYPE;
    }
}