package com.tennisscoring.domain.service;

import com.tennisscoring.domain.event.MatchCompletedEvent;
import com.tennisscoring.domain.event.MatchCreatedEvent;
import com.tennisscoring.domain.event.PointScoredEvent;
import com.tennisscoring.domain.model.Game;
import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.PlayerId;
import com.tennisscoring.domain.model.Set;
import com.tennisscoring.ports.secondary.MatchEventPublisherPort;
import com.tennisscoring.ports.secondary.GameEventPublisherPort;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service responsible for handling all match-related event publishing.
 * 負責處理所有比賽相關事件發布的服務
 * 
 * This service follows the Single Responsibility Principle by focusing
 * solely on event publishing operations.
 * 
 * Requirements: 10.4
 */
@Service
public class MatchEventService implements EventService {
    
    private final MatchEventPublisherPort matchEventPublisher;
    private final GameEventPublisherPort gameEventPublisher;
    
    public MatchEventService(MatchEventPublisherPort matchEventPublisher, 
                           GameEventPublisherPort gameEventPublisher) {
        this.matchEventPublisher = Objects.requireNonNull(matchEventPublisher, "Match event publisher cannot be null");
        this.gameEventPublisher = Objects.requireNonNull(gameEventPublisher, "Game event publisher cannot be null");
    }
    
    /**
     * Publish match created event.
     * 發布比賽創建事件
     * 
     * @param match the created match
     */
    public void publishMatchCreated(Match match) {
        Objects.requireNonNull(match, "Match cannot be null");
        
        MatchCreatedEvent event = new MatchCreatedEvent(
            match.getMatchId(),
            match.getPlayer1().getName(),
            match.getPlayer2().getName()
        );
        
        matchEventPublisher.publishMatchCreated(event);
    }
    
    /**
     * Publish point scored event.
     * 發布得分事件
     * 
     * @param match the match where point was scored
     * @param playerId the player who scored
     */
    public void publishPointScored(Match match, String playerId) {
        Objects.requireNonNull(match, "Match cannot be null");
        Objects.requireNonNull(playerId, "Player ID cannot be null");
        
        PointScoredEvent event = new PointScoredEvent(
            match.getMatchId(),
            playerId,
            match.getCurrentScore(),
            match.getCurrentSetNumber(),
            match.getCurrentGameNumber()
        );
        
        gameEventPublisher.publishPointScored(event);
    }
    
    /**
     * Publish match completed event.
     * 發布比賽完成事件
     * 
     * @param match the completed match
     */
    public void publishMatchCompleted(Match match) {
        Objects.requireNonNull(match, "Match cannot be null");
        
        if (!match.isCompleted()) {
            throw new IllegalArgumentException("Match is not completed");
        }
        
        MatchCompletedEvent event = new MatchCompletedEvent(
            match.getMatchId(),
            match.getWinner().getValue(),
            match.getCurrentScore(),
            match.getSets().size()
        );
        
        matchEventPublisher.publishMatchCompleted(event);
    }
    
    /**
     * Publish match deleted event.
     * 發布比賽刪除事件
     * 
     * @param matchId the ID of the deleted match
     * @param deletedBy who deleted the match
     */
    public void publishMatchDeleted(String matchId, String deletedBy) {
        Objects.requireNonNull(matchId, "Match ID cannot be null");
        
        matchEventPublisher.publishMatchDeleted(matchId, deletedBy);
    }
    
    /**
     * Publish all relevant events based on game state changes.
     * 根據遊戲狀態變化發布所有相關事件
     * 
     * @param match the match with state changes
     * @param playerId the player who caused the state change
     */
    public void publishGameStateEvents(Match match, PlayerId playerId) {
        Objects.requireNonNull(match, "Match cannot be null");
        Objects.requireNonNull(playerId, "Player ID cannot be null");
        
        try {
            // Check if a game was completed
            Game currentGame = match.getCurrentGame();
            if (currentGame != null && currentGame.isCompleted()) {
                String winnerId = currentGame.getWinner().getValue();
                gameEventPublisher.publishGameCompleted(
                    match.getMatchId(), 
                    currentGame.getGameNumber(), 
                    winnerId
                );
            }
            
            // Check if a set was completed
            Set currentSet = match.getCurrentSet();
            if (currentSet != null && currentSet.isCompleted()) {
                String winnerId = currentSet.getWinner().getValue();
                gameEventPublisher.publishSetCompleted(
                    match.getMatchId(), 
                    currentSet.getSetNumber(), 
                    winnerId
                );
            }
        } catch (Exception e) {
            // Log error but don't fail the operation
            // In a real implementation, we would use a proper logger
            System.err.println("Error publishing game state events: " + e.getMessage());
        }
    }
    
    /**
     * Publish game completed event.
     * 發布局完成事件
     * 
     * @param matchId the match ID
     * @param gameNumber the completed game number
     * @param winnerId the winner of the game
     */
    public void publishGameCompleted(String matchId, int gameNumber, String winnerId) {
        Objects.requireNonNull(matchId, "Match ID cannot be null");
        Objects.requireNonNull(winnerId, "Winner ID cannot be null");
        
        gameEventPublisher.publishGameCompleted(matchId, gameNumber, winnerId);
    }
    
    /**
     * Publish set completed event.
     * 發布盤完成事件
     * 
     * @param matchId the match ID
     * @param setNumber the completed set number
     * @param winnerId the winner of the set
     */
    public void publishSetCompleted(String matchId, int setNumber, String winnerId) {
        Objects.requireNonNull(matchId, "Match ID cannot be null");
        Objects.requireNonNull(winnerId, "Winner ID cannot be null");
        
        gameEventPublisher.publishSetCompleted(matchId, setNumber, winnerId);
    }
}