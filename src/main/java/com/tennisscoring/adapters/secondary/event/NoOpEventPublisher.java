package com.tennisscoring.adapters.secondary.event;

import com.tennisscoring.domain.event.MatchCreatedEvent;
import com.tennisscoring.domain.event.MatchCompletedEvent;
import com.tennisscoring.domain.event.PointScoredEvent;
import com.tennisscoring.ports.secondary.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * No-operation implementation of EventPublisherPort that logs events instead of publishing them.
 * 事件發布器的空實作，將事件記錄到日誌而不是實際發布
 * 
 * This implementation provides a simple logging-based event publisher suitable for
 * development and testing environments. In production, this can be replaced with
 * a real event publishing mechanism (e.g., message queues, event streams).
 * 
 * Requirements: 6.3
 */
@Component
public class NoOpEventPublisher implements EventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(NoOpEventPublisher.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void publishMatchCreated(MatchCreatedEvent event) {
        if (event == null) {
            logger.warn("Attempted to publish null MatchCreatedEvent");
            return;
        }
        
        logger.info("Match created event: matchId={}, player1={}, player2={}, eventId={}, occurredAt={}", 
                event.getMatchId(), 
                event.getPlayer1Name(), 
                event.getPlayer2Name(),
                event.getEventId(),
                event.getOccurredAt());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void publishPointScored(PointScoredEvent event) {
        if (event == null) {
            logger.warn("Attempted to publish null PointScoredEvent");
            return;
        }
        
        logger.info("Point scored event: matchId={}, playerId={}, currentScore={}, set={}, game={}, eventId={}, occurredAt={}", 
                event.getMatchId(), 
                event.getPlayerId(), 
                event.getCurrentScore(),
                event.getCurrentSet(),
                event.getCurrentGame(),
                event.getEventId(),
                event.getOccurredAt());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void publishMatchCompleted(MatchCompletedEvent event) {
        if (event == null) {
            logger.warn("Attempted to publish null MatchCompletedEvent");
            return;
        }
        
        logger.info("Match completed event: matchId={}, winnerId={}, finalScore={}, totalSets={}, eventId={}, occurredAt={}", 
                event.getMatchId(), 
                event.getWinnerId(), 
                event.getFinalScore(),
                event.getTotalSets(),
                event.getEventId(),
                event.getOccurredAt());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void publishMatchDeleted(String matchId, String deletedBy) {
        if (matchId == null || matchId.trim().isEmpty()) {
            logger.warn("Attempted to publish match deleted event with null or empty matchId");
            return;
        }
        
        logger.info("Match deleted event: matchId={}, deletedBy={}", 
                matchId, 
                deletedBy != null ? deletedBy : "unknown");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void publishGameCompleted(String matchId, int gameNumber, String winnerId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            logger.warn("Attempted to publish game completed event with null or empty matchId");
            return;
        }
        
        if (winnerId == null || winnerId.trim().isEmpty()) {
            logger.warn("Attempted to publish game completed event with null or empty winnerId");
            return;
        }
        
        logger.info("Game completed event: matchId={}, gameNumber={}, winnerId={}", 
                matchId, 
                gameNumber, 
                winnerId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void publishSetCompleted(String matchId, int setNumber, String winnerId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            logger.warn("Attempted to publish set completed event with null or empty matchId");
            return;
        }
        
        if (winnerId == null || winnerId.trim().isEmpty()) {
            logger.warn("Attempted to publish set completed event with null or empty winnerId");
            return;
        }
        
        logger.info("Set completed event: matchId={}, setNumber={}, winnerId={}", 
                matchId, 
                setNumber, 
                winnerId);
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