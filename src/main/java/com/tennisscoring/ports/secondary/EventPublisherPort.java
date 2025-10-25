package com.tennisscoring.ports.secondary;

import com.tennisscoring.domain.event.MatchCreatedEvent;
import com.tennisscoring.domain.event.MatchCompletedEvent;
import com.tennisscoring.domain.event.PointScoredEvent;

/**
 * Secondary Port for Event Publishing Operations
 * 事件發布操作的次要埠介面
 * 
 * This interface defines the outbound operations for publishing domain events
 * that occur during tennis match operations. It allows the domain to notify
 * external systems about important business events.
 * 
 * Requirements: 1.3, 6.3
 */
public interface EventPublisherPort {
    
    /**
     * Publishes an event when a new match is created
     * 發布比賽創建事件
     * 
     * @param event The match created event containing match details
     */
    void publishMatchCreated(MatchCreatedEvent event);
    
    /**
     * Publishes an event when a point is scored in a match
     * 發布比賽得分事件
     * 
     * @param event The point scored event containing scoring details
     */
    void publishPointScored(PointScoredEvent event);
    
    /**
     * Publishes an event when a match is completed
     * 發布比賽完成事件
     * 
     * @param event The match completed event containing final results
     */
    void publishMatchCompleted(MatchCompletedEvent event);
    
    /**
     * Publishes an event when a match is deleted
     * 發布比賽刪除事件
     * 
     * @param matchId The ID of the deleted match
     * @param deletedBy The identifier of who deleted the match (optional)
     */
    void publishMatchDeleted(String matchId, String deletedBy);
    
    /**
     * Publishes an event when a game is completed within a match
     * 發布局完成事件
     * 
     * @param matchId The ID of the match
     * @param gameNumber The number of the completed game
     * @param winnerId The ID of the player who won the game
     */
    void publishGameCompleted(String matchId, int gameNumber, String winnerId);
    
    /**
     * Publishes an event when a set is completed within a match
     * 發布盤完成事件
     * 
     * @param matchId The ID of the match
     * @param setNumber The number of the completed set
     * @param winnerId The ID of the player who won the set
     */
    void publishSetCompleted(String matchId, int setNumber, String winnerId);
}