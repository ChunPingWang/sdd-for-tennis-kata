package com.tennisscoring.domain.strategy;

import com.tennisscoring.domain.model.Game;
import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.PlayerId;

/**
 * Strategy interface for different scoring systems.
 * 不同計分系統的策略介面
 * 
 * This interface follows the Open-Closed Principle by allowing
 * new scoring strategies to be added without modifying existing code.
 * 
 * Requirements: 2.1, 2.3, 5.1, 5.2
 */
public interface ScoringStrategy {
    
    /**
     * Score a point for the specified player in the given game.
     * 為指定球員在給定局中記分
     * 
     * @param game the game where the point is scored
     * @param playerId the player who scored the point
     * @return true if the game is completed after this point
     */
    boolean scorePoint(Game game, PlayerId playerId);
    
    /**
     * Check if the game is in deuce state.
     * 檢查局是否處於平分狀態
     * 
     * @param game the game to check
     * @return true if the game is in deuce
     */
    boolean isDeuce(Game game);
    
    /**
     * Check if a player has advantage.
     * 檢查球員是否有優勢
     * 
     * @param game the game to check
     * @param playerId the player to check
     * @return true if the player has advantage
     */
    boolean hasAdvantage(Game game, PlayerId playerId);
    
    /**
     * Get the current score display for the game.
     * 獲取局的當前比分顯示
     * 
     * @param game the game
     * @return formatted score string
     */
    String getScoreDisplay(Game game);
    
    /**
     * Check if this strategy applies to the given game.
     * 檢查此策略是否適用於給定的局
     * 
     * @param game the game to check
     * @return true if this strategy should be used for the game
     */
    boolean appliesTo(Game game);
}