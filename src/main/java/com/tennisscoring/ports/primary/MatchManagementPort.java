package com.tennisscoring.ports.primary;

import com.tennisscoring.domain.model.Match;

/**
 * Primary Port for Match Management Operations
 * 比賽管理操作的主要埠介面
 * 
 * This interface defines the inbound operations for managing tennis matches,
 * including creating matches, scoring points, and deleting matches.
 * 
 * Requirements: 1.1, 2.1, 7.1
 */
public interface MatchManagementPort {
    
    /**
     * Creates a new tennis match between two players
     * 創建兩名球員之間的新網球比賽
     * 
     * @param player1Name Name of the first player
     * @param player2Name Name of the second player
     * @return The created match
     * @throws IllegalArgumentException if player names are invalid
     */
    Match createMatch(String player1Name, String player2Name);
    
    /**
     * Records a point scored by a player in a match
     * 記錄球員在比賽中的得分
     * 
     * @param matchId The unique identifier of the match
     * @param playerId The unique identifier of the player who scored
     * @return The updated match with new score
     * @throws MatchNotFoundException if match is not found
     * @throws InvalidMatchStateException if match is already completed
     * @throws IllegalArgumentException if player is not in the match
     */
    Match scorePoint(String matchId, String playerId);
    
    /**
     * Deletes a match from the system
     * 從系統中刪除比賽
     * 
     * @param matchId The unique identifier of the match to delete
     * @throws MatchNotFoundException if match is not found
     */
    void deleteMatch(String matchId);
}