package com.tennisscoring.adapters.secondary.event;

import com.tennisscoring.domain.event.MatchCreatedEvent;
import com.tennisscoring.domain.event.MatchCompletedEvent;
import com.tennisscoring.domain.event.PointScoredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * No-operation implementation of EventPublisherPort that logs events instead of publishing them.
 * 事件發布器的空實作，將事件記錄到日誌而不是實際發布
 * 
 * This implementation follows the Liskov Substitution Principle by extending
 * BaseEventPublisher and can be substituted with any other event publisher implementation
 * without breaking functionality.
 * 
 * Requirements: 6.3
 */
@Component
public class NoOpEventPublisher extends BaseEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(NoOpEventPublisher.class);
    
    @Override
    protected void doPublishMatchCreated(MatchCreatedEvent event) {
        logger.info("Match created event: matchId={}, player1={}, player2={}, eventId={}, occurredAt={}", 
                event.getMatchId(), 
                event.getPlayer1Name(), 
                event.getPlayer2Name(),
                event.getEventId(),
                event.getOccurredAt());
    }
    
    @Override
    protected void doPublishPointScored(PointScoredEvent event) {
        logger.info("Point scored event: matchId={}, playerId={}, currentScore={}, set={}, game={}, eventId={}, occurredAt={}", 
                event.getMatchId(), 
                event.getPlayerId(), 
                event.getCurrentScore(),
                event.getCurrentSet(),
                event.getCurrentGame(),
                event.getEventId(),
                event.getOccurredAt());
    }
    
    @Override
    protected void doPublishMatchCompleted(MatchCompletedEvent event) {
        logger.info("Match completed event: matchId={}, winnerId={}, finalScore={}, totalSets={}, eventId={}, occurredAt={}", 
                event.getMatchId(), 
                event.getWinnerId(), 
                event.getFinalScore(),
                event.getTotalSets(),
                event.getEventId(),
                event.getOccurredAt());
    }
    
    @Override
    protected void doPublishMatchDeleted(String matchId, String deletedBy) {
        logger.info("Match deleted event: matchId={}, deletedBy={}", 
                matchId, 
                deletedBy != null ? deletedBy : "unknown");
    }
    
    @Override
    protected void doPublishGameCompleted(String matchId, int gameNumber, String winnerId) {
        logger.info("Game completed event: matchId={}, gameNumber={}, winnerId={}", 
                matchId, 
                gameNumber, 
                winnerId);
    }
    
    @Override
    protected void doPublishSetCompleted(String matchId, int setNumber, String winnerId) {
        logger.info("Set completed event: matchId={}, setNumber={}, winnerId={}", 
                matchId, 
                setNumber, 
                winnerId);
    }
    
    @Override
    public String getPublisherType() {
        return "NO_OP";
    }
    
    @Override
    public boolean isAsynchronous() {
        return false;
    }
    
    /**
     * Get the logger instance for testing purposes.
     * 取得日誌記錄器實例，主要用於測試
     * 
     * @return the logger instance
     */
    protected Logger getLogger() {
        return logger;
    }
}