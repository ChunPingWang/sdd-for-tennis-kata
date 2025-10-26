package com.tennisscoring.ports.secondary;

import com.tennisscoring.domain.event.PointScoredEvent;

/**
 * Port for publishing game-level events.
 * 發布局級別事件的埠介面
 * 
 * This interface follows the Interface Segregation Principle by focusing
 * solely on game-level event publishing.
 * 
 * Requirements: 6.3
 */
public interface GameEventPublisherPort {
    
    /**
     * Publishes an event when a point is scored in a match.
     * 發布比賽得分事件
     * 
     * @param event The point scored event containing scoring details
     */
    void publishPointScored(PointScoredEvent event);
    
    /**
     * Publishes an event when a game is completed within a match.
     * 發布局完成事件
     * 
     * @param matchId The ID of the match
     * @param gameNumber The number of the completed game
     * @param winnerId The ID of the player who won the game
     */
    void publishGameCompleted(String matchId, int gameNumber, String winnerId);
    
    /**
     * Publishes an event when a set is completed within a match.
     * 發布盤完成事件
     * 
     * @param matchId The ID of the match
     * @param setNumber The number of the completed set
     * @param winnerId The ID of the player who won the set
     */
    void publishSetCompleted(String matchId, int setNumber, String winnerId);
}