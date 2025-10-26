package com.tennisscoring.ports.secondary;

import com.tennisscoring.domain.event.MatchCompletedEvent;
import com.tennisscoring.domain.event.MatchCreatedEvent;

/**
 * Port for publishing match-level events.
 * 發布比賽級別事件的埠介面
 * 
 * This interface follows the Interface Segregation Principle by focusing
 * solely on match-level event publishing.
 * 
 * Requirements: 6.3
 */
public interface MatchEventPublisherPort {
    
    /**
     * Publishes an event when a new match is created.
     * 發布比賽創建事件
     * 
     * @param event The match created event containing match details
     */
    void publishMatchCreated(MatchCreatedEvent event);
    
    /**
     * Publishes an event when a match is completed.
     * 發布比賽完成事件
     * 
     * @param event The match completed event containing final results
     */
    void publishMatchCompleted(MatchCompletedEvent event);
    
    /**
     * Publishes an event when a match is deleted.
     * 發布比賽刪除事件
     * 
     * @param matchId The ID of the deleted match
     * @param deletedBy The identifier of who deleted the match (optional)
     */
    void publishMatchDeleted(String matchId, String deletedBy);
}