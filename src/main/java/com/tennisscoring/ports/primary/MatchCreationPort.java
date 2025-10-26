package com.tennisscoring.ports.primary;

import com.tennisscoring.domain.model.Match;

/**
 * Port for match creation operations.
 * 比賽創建操作的埠介面
 * 
 * This interface follows the Interface Segregation Principle by focusing
 * solely on match creation functionality.
 * 
 * Requirements: 1.1, 2.1
 */
public interface MatchCreationPort {
    
    /**
     * Creates a new tennis match between two players.
     * 創建兩名球員之間的新網球比賽
     * 
     * @param player1Name Name of the first player
     * @param player2Name Name of the second player
     * @return The created match
     * @throws IllegalArgumentException if player names are invalid
     */
    Match createMatch(String player1Name, String player2Name);
    
    /**
     * Creates a new tennis match with a specific type.
     * 創建特定類型的新網球比賽
     * 
     * @param matchType The type of match to create
     * @param player1Name Name of the first player
     * @param player2Name Name of the second player
     * @return The created match
     * @throws IllegalArgumentException if parameters are invalid
     */
    Match createMatch(String matchType, String player1Name, String player2Name);
}