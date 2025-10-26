package com.tennisscoring.domain.service;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;

import java.util.List;

/**
 * Interface for match domain service operations.
 * 比賽領域服務操作的介面
 * 
 * This interface follows the Dependency Inversion Principle by providing
 * an abstraction that higher-level modules can depend on instead of
 * concrete implementations.
 * 
 * Requirements: 10.4, 10.5
 */
public interface MatchService {
    
    /**
     * Creates a new tennis match between two players.
     * 創建兩名球員之間的新網球比賽
     * 
     * @param player1Name Name of the first player
     * @param player2Name Name of the second player
     * @return The created match
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
     */
    Match createMatch(String matchType, String player1Name, String player2Name);
    
    /**
     * Records a point scored by a player in a match.
     * 記錄球員在比賽中的得分
     * 
     * @param matchId The unique identifier of the match
     * @param playerId The unique identifier of the player who scored
     * @return The updated match with new score
     */
    Match scorePoint(String matchId, String playerId);
    
    /**
     * Deletes a match from the system.
     * 從系統中刪除比賽
     * 
     * @param matchId The unique identifier of the match to delete
     */
    void deleteMatch(String matchId);
    
    /**
     * Cancels a match that is in progress.
     * 取消進行中的比賽
     * 
     * @param matchId The unique identifier of the match to cancel
     * @return The cancelled match
     */
    Match cancelMatch(String matchId);
    
    /**
     * Updates an existing match.
     * 更新現有比賽
     * 
     * @param match The match to update
     * @return The updated match
     */
    Match updateMatch(Match match);
    
    /**
     * Retrieves a match by its unique identifier.
     * 根據唯一識別碼檢索比賽
     * 
     * @param matchId The unique identifier of the match
     * @return The match with the specified ID
     */
    Match getMatch(String matchId);
    
    /**
     * Retrieves all matches in the system.
     * 檢索系統中的所有比賽
     * 
     * @return List of all matches
     */
    List<Match> getAllMatches();
    
    /**
     * Retrieves matches by their status.
     * 根據狀態檢索比賽
     * 
     * @param status The match status to filter by
     * @return List of matches with the specified status
     */
    List<Match> getMatchesByStatus(MatchStatus status);
    
    /**
     * Checks if a match exists with the given ID.
     * 檢查是否存在具有給定ID的比賽
     * 
     * @param matchId The match ID to check
     * @return true if match exists, false otherwise
     */
    boolean matchExists(String matchId);
}