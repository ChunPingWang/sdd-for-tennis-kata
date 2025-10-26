package com.tennisscoring.adapters.secondary.event;

import com.tennisscoring.domain.event.MatchCompletedEvent;
import com.tennisscoring.domain.event.MatchCreatedEvent;
import com.tennisscoring.domain.event.PointScoredEvent;
import com.tennisscoring.ports.secondary.EventPublisherPort;
import com.tennisscoring.ports.secondary.MatchEventPublisherPort;
import com.tennisscoring.ports.secondary.GameEventPublisherPort;

import java.util.Objects;

/**
 * Abstract base class for event publisher implementations.
 * 事件發布器實作的抽象基底類別
 * 
 * This class follows the Liskov Substitution Principle by providing
 * a common contract and shared behavior that all event publisher implementations
 * must follow. Subclasses can be substituted without breaking functionality.
 * 
 * Requirements: 1.3, 6.3
 */
public abstract class BaseEventPublisher implements EventPublisherPort, MatchEventPublisherPort, GameEventPublisherPort {
    
    /**
     * Template method for publishing match created events with validation.
     * 發布比賽創建事件的模板方法，包含驗證
     * 
     * @param event the match created event
     */
    @Override
    public final void publishMatchCreated(MatchCreatedEvent event) {
        validateMatchCreatedEvent(event);
        
        doPublishMatchCreated(event);
    }
    
    /**
     * Template method for publishing point scored events with validation.
     * 發布得分事件的模板方法，包含驗證
     * 
     * @param event the point scored event
     */
    @Override
    public final void publishPointScored(PointScoredEvent event) {
        validatePointScoredEvent(event);
        
        doPublishPointScored(event);
    }
    
    /**
     * Template method for publishing match completed events with validation.
     * 發布比賽完成事件的模板方法，包含驗證
     * 
     * @param event the match completed event
     */
    @Override
    public final void publishMatchCompleted(MatchCompletedEvent event) {
        validateMatchCompletedEvent(event);
        
        doPublishMatchCompleted(event);
    }
    
    /**
     * Template method for publishing match deleted events with validation.
     * 發布比賽刪除事件的模板方法，包含驗證
     * 
     * @param matchId the match ID
     * @param deletedBy who deleted the match
     */
    @Override
    public final void publishMatchDeleted(String matchId, String deletedBy) {
        validateMatchId(matchId);
        
        doPublishMatchDeleted(matchId, deletedBy);
    }
    
    /**
     * Template method for publishing game completed events with validation.
     * 發布局完成事件的模板方法，包含驗證
     * 
     * @param matchId the match ID
     * @param gameNumber the game number
     * @param winnerId the winner ID
     */
    @Override
    public final void publishGameCompleted(String matchId, int gameNumber, String winnerId) {
        validateMatchId(matchId);
        validateWinnerId(winnerId);
        validateGameNumber(gameNumber);
        
        doPublishGameCompleted(matchId, gameNumber, winnerId);
    }
    
    /**
     * Template method for publishing set completed events with validation.
     * 發布盤完成事件的模板方法，包含驗證
     * 
     * @param matchId the match ID
     * @param setNumber the set number
     * @param winnerId the winner ID
     */
    @Override
    public final void publishSetCompleted(String matchId, int setNumber, String winnerId) {
        validateMatchId(matchId);
        validateWinnerId(winnerId);
        validateSetNumber(setNumber);
        
        doPublishSetCompleted(matchId, setNumber, winnerId);
    }
    
    // Abstract methods that subclasses must implement
    
    /**
     * Perform the actual match created event publishing.
     * 執行實際的比賽創建事件發布
     * 
     * @param event the match created event
     */
    protected abstract void doPublishMatchCreated(MatchCreatedEvent event);
    
    /**
     * Perform the actual point scored event publishing.
     * 執行實際的得分事件發布
     * 
     * @param event the point scored event
     */
    protected abstract void doPublishPointScored(PointScoredEvent event);
    
    /**
     * Perform the actual match completed event publishing.
     * 執行實際的比賽完成事件發布
     * 
     * @param event the match completed event
     */
    protected abstract void doPublishMatchCompleted(MatchCompletedEvent event);
    
    /**
     * Perform the actual match deleted event publishing.
     * 執行實際的比賽刪除事件發布
     * 
     * @param matchId the match ID
     * @param deletedBy who deleted the match
     */
    protected abstract void doPublishMatchDeleted(String matchId, String deletedBy);
    
    /**
     * Perform the actual game completed event publishing.
     * 執行實際的局完成事件發布
     * 
     * @param matchId the match ID
     * @param gameNumber the game number
     * @param winnerId the winner ID
     */
    protected abstract void doPublishGameCompleted(String matchId, int gameNumber, String winnerId);
    
    /**
     * Perform the actual set completed event publishing.
     * 執行實際的盤完成事件發布
     * 
     * @param matchId the match ID
     * @param setNumber the set number
     * @param winnerId the winner ID
     */
    protected abstract void doPublishSetCompleted(String matchId, int setNumber, String winnerId);
    
    // Common validation methods
    
    /**
     * Validate a match created event.
     * 驗證比賽創建事件
     * 
     * @param event the event to validate
     */
    protected void validateMatchCreatedEvent(MatchCreatedEvent event) {
        Objects.requireNonNull(event, "Match created event cannot be null");
        validateMatchId(event.getMatchId());
        
        if (event.getPlayer1Name() == null || event.getPlayer1Name().trim().isEmpty()) {
            throw new IllegalArgumentException("Player 1 name cannot be null or empty");
        }
        
        if (event.getPlayer2Name() == null || event.getPlayer2Name().trim().isEmpty()) {
            throw new IllegalArgumentException("Player 2 name cannot be null or empty");
        }
    }
    
    /**
     * Validate a point scored event.
     * 驗證得分事件
     * 
     * @param event the event to validate
     */
    protected void validatePointScoredEvent(PointScoredEvent event) {
        Objects.requireNonNull(event, "Point scored event cannot be null");
        validateMatchId(event.getMatchId());
        
        if (event.getPlayerId() == null || event.getPlayerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
    }
    
    /**
     * Validate a match completed event.
     * 驗證比賽完成事件
     * 
     * @param event the event to validate
     */
    protected void validateMatchCompletedEvent(MatchCompletedEvent event) {
        Objects.requireNonNull(event, "Match completed event cannot be null");
        validateMatchId(event.getMatchId());
        validateWinnerId(event.getWinnerId());
    }
    
    /**
     * Validate a match ID.
     * 驗證比賽ID
     * 
     * @param matchId the match ID to validate
     */
    protected void validateMatchId(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
    }
    
    /**
     * Validate a winner ID.
     * 驗證獲勝者ID
     * 
     * @param winnerId the winner ID to validate
     */
    protected void validateWinnerId(String winnerId) {
        if (winnerId == null || winnerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Winner ID cannot be null or empty");
        }
    }
    
    /**
     * Validate a game number.
     * 驗證局號
     * 
     * @param gameNumber the game number to validate
     */
    protected void validateGameNumber(int gameNumber) {
        if (gameNumber < 1) {
            throw new IllegalArgumentException("Game number must be positive");
        }
    }
    
    /**
     * Validate a set number.
     * 驗證盤號
     * 
     * @param setNumber the set number to validate
     */
    protected void validateSetNumber(int setNumber) {
        if (setNumber < 1) {
            throw new IllegalArgumentException("Set number must be positive");
        }
    }
    
    /**
     * Get publisher type for logging/debugging purposes.
     * 獲取發布器類型用於日誌/除錯目的
     * 
     * @return the publisher type name
     */
    public abstract String getPublisherType();
    
    /**
     * Check if this publisher supports asynchronous publishing.
     * 檢查此發布器是否支援非同步發布
     * 
     * @return true if asynchronous
     */
    public abstract boolean isAsynchronous();
}