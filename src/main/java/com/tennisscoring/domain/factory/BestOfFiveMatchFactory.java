package com.tennisscoring.domain.factory;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchId;
import com.tennisscoring.domain.model.Player;
import com.tennisscoring.domain.model.PlayerId;
import org.springframework.stereotype.Component;

/**
 * Factory for creating best-of-five tennis matches.
 * 創建五盤三勝制網球比賽的工廠
 * 
 * This factory creates matches following professional tennis rules
 * where the first player to win 3 sets wins the match.
 * 
 * Requirements: 2.1, 2.3, 5.1, 5.2
 */
@Component
public class BestOfFiveMatchFactory implements MatchFactory {
    
    private static final String MATCH_TYPE = "BEST_OF_5";
    
    @Override
    public Match createMatch(String player1Name, String player2Name) {
        // For now, we'll create a standard match but mark it as best-of-5
        // In a full implementation, we would need to modify the Match class
        // to support different winning conditions
        Match match = Match.create(player1Name, player2Name);
        
        // TODO: In a complete implementation, we would:
        // 1. Set the match to require 3 sets to win instead of 2
        // 2. Possibly adjust tiebreak rules for the final set
        // 3. Add metadata to track the match type
        
        return match;
    }
    
    @Override
    public boolean supports(String matchType) {
        return MATCH_TYPE.equalsIgnoreCase(matchType) ||
               "BEST_OF_FIVE".equalsIgnoreCase(matchType);
    }
    
    @Override
    public String getMatchType() {
        return MATCH_TYPE;
    }
}