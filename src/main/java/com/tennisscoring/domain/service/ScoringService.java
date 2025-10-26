package com.tennisscoring.domain.service;

import com.tennisscoring.domain.model.GameScore;
import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.PlayerId;

/**
 * Interface for scoring domain service operations.
 * 計分領域服務操作的介面
 * 
 * This interface follows the Dependency Inversion Principle by providing
 * an abstraction that higher-level modules can depend on instead of
 * concrete implementations.
 * 
 * Requirements: 10.4, 10.5
 */
public interface ScoringService {
    
    /**
     * Process a point scored by a player in a match.
     * 處理球員在比賽中的得分
     * 
     * @param match the match where the point was scored
     * @param playerId the ID of the player who scored
     * @return true if the match is completed after this point
     */
    boolean scorePoint(Match match, PlayerId playerId);
    
    /**
     * Calculate the current score summary for display.
     * 計算用於顯示的當前比分摘要
     * 
     * @param match the match to get score for
     * @return formatted score string
     */
    String calculateCurrentScore(Match match);
    
    /**
     * Check if the current game is in deuce state.
     * 檢查當前局是否處於平分狀態
     * 
     * @param match the match to check
     * @return true if current game is in deuce
     */
    boolean isCurrentGameDeuce(Match match);
    
    /**
     * Check if a player has advantage in the current game.
     * 檢查球員在當前局是否有優勢
     * 
     * @param match the match to check
     * @param playerId the player to check for advantage
     * @return true if player has advantage
     */
    boolean hasAdvantage(Match match, PlayerId playerId);
    
    /**
     * Check if the current game is a tiebreak.
     * 檢查當前局是否為搶七局
     * 
     * @param match the match to check
     * @return true if current game is a tiebreak
     */
    boolean isCurrentGameTiebreak(Match match);
    
    /**
     * Get the current game score for a specific player.
     * 獲取特定球員的當前局分數
     * 
     * @param match the match to get score from
     * @param playerId the player's ID
     * @return the player's current game score, or null if not available
     */
    GameScore getCurrentGameScore(Match match, PlayerId playerId);
    
    /**
     * Get the current tiebreak score for a specific player.
     * 獲取特定球員的當前搶七局分數
     * 
     * @param match the match to get score from
     * @param playerId the player's ID
     * @return the player's current tiebreak score, or -1 if not in tiebreak
     */
    int getCurrentTiebreakScore(Match match, PlayerId playerId);
}