package com.tennisscoring.domain.factory;

import com.tennisscoring.domain.model.Match;

/**
 * Factory interface for creating different types of matches.
 * 創建不同類型比賽的工廠介面
 * 
 * This interface follows the Open-Closed Principle by allowing
 * new match types to be added without modifying existing code.
 * 
 * Requirements: 2.1, 2.3, 5.1, 5.2
 */
public interface MatchFactory {
    
    /**
     * Create a match with the specified configuration.
     * 使用指定配置創建比賽
     * 
     * @param player1Name name of the first player
     * @param player2Name name of the second player
     * @return the created match
     */
    Match createMatch(String player1Name, String player2Name);
    
    /**
     * Check if this factory can create matches of the specified type.
     * 檢查此工廠是否可以創建指定類型的比賽
     * 
     * @param matchType the type of match
     * @return true if this factory supports the match type
     */
    boolean supports(String matchType);
    
    /**
     * Get the match type this factory creates.
     * 獲取此工廠創建的比賽類型
     * 
     * @return the match type identifier
     */
    String getMatchType();
}