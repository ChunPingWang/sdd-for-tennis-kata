package com.tennisscoring.domain.service;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.PlayerId;

/**
 * Interface for event service operations.
 * 事件服務操作的介面
 * 
 * This interface follows the Dependency Inversion Principle by providing
 * an abstraction that higher-level modules can depend on instead of
 * concrete implementations.
 * 
 * Requirements: 10.4
 */
public interface EventService {
    
    /**
     * Publish match created event.
     * 發布比賽創建事件
     * 
     * @param match the created match
     */
    void publishMatchCreated(Match match);
    
    /**
     * Publish point scored event.
     * 發布得分事件
     * 
     * @param match the match where point was scored
     * @param playerId the player who scored
     */
    void publishPointScored(Match match, String playerId);
    
    /**
     * Publish match completed event.
     * 發布比賽完成事件
     * 
     * @param match the completed match
     */
    void publishMatchCompleted(Match match);
    
    /**
     * Publish match deleted event.
     * 發布比賽刪除事件
     * 
     * @param matchId the ID of the deleted match
     * @param deletedBy who deleted the match
     */
    void publishMatchDeleted(String matchId, String deletedBy);
    
    /**
     * Publish all relevant events based on game state changes.
     * 根據遊戲狀態變化發布所有相關事件
     * 
     * @param match the match with state changes
     * @param playerId the player who caused the state change
     */
    void publishGameStateEvents(Match match, PlayerId playerId);
    
    /**
     * Publish game completed event.
     * 發布局完成事件
     * 
     * @param matchId the match ID
     * @param gameNumber the completed game number
     * @param winnerId the winner of the game
     */
    void publishGameCompleted(String matchId, int gameNumber, String winnerId);
    
    /**
     * Publish set completed event.
     * 發布盤完成事件
     * 
     * @param matchId the match ID
     * @param setNumber the completed set number
     * @param winnerId the winner of the set
     */
    void publishSetCompleted(String matchId, int setNumber, String winnerId);
}