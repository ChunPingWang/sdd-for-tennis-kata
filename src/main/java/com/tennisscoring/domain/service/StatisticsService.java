package com.tennisscoring.domain.service;

import com.tennisscoring.domain.model.PlayerId;

/**
 * Interface for statistics service operations.
 * 統計服務操作的介面
 * 
 * This interface follows the Dependency Inversion Principle by providing
 * an abstraction that higher-level modules can depend on instead of
 * concrete implementations.
 * 
 * Requirements: 10.4
 */
public interface StatisticsService {
    
    /**
     * Get match statistics summary for a specific match.
     * 獲取特定比賽的統計摘要
     * 
     * @param matchId the match ID
     * @return match statistics
     */
    MatchStatisticsService.MatchStatistics getMatchStatistics(String matchId);
    
    /**
     * Get system-wide match statistics.
     * 獲取系統範圍的比賽統計
     * 
     * @return system statistics
     */
    MatchStatisticsService.SystemStatistics getSystemStatistics();
    
    /**
     * Get the current score summary for a match.
     * 獲取比賽的當前比分摘要
     * 
     * @param matchId the match ID
     * @return formatted score string
     */
    String getCurrentScore(String matchId);
    
    /**
     * Check if a match is in deuce state.
     * 檢查比賽是否處於平分狀態
     * 
     * @param matchId the match ID
     * @return true if current game is in deuce
     */
    boolean isMatchInDeuce(String matchId);
    
    /**
     * Check if a player has advantage in the current game.
     * 檢查球員在當前局是否有優勢
     * 
     * @param matchId the match ID
     * @param playerId the player ID
     * @return true if player has advantage
     */
    boolean playerHasAdvantage(String matchId, String playerId);
    
    /**
     * Check if the current game is a tiebreak.
     * 檢查當前局是否為搶七局
     * 
     * @param matchId the match ID
     * @return true if current game is a tiebreak
     */
    boolean isCurrentGameTiebreak(String matchId);
}