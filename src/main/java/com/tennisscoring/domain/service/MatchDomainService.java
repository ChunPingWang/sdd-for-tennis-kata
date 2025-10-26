package com.tennisscoring.domain.service;

import com.tennisscoring.domain.event.MatchCompletedEvent;
import com.tennisscoring.domain.event.MatchCreatedEvent;
import com.tennisscoring.domain.event.PointScoredEvent;
import com.tennisscoring.domain.exception.InvalidMatchStateException;
import com.tennisscoring.domain.exception.MatchNotFoundException;
import com.tennisscoring.domain.model.*;
import com.tennisscoring.ports.primary.MatchManagementPort;
import com.tennisscoring.ports.primary.QueryPort;
import com.tennisscoring.ports.secondary.EventPublisherPort;
import com.tennisscoring.ports.secondary.MatchRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Domain service implementing match management and query operations.
 * 實作比賽管理和查詢操作的領域服務
 * 
 * This service acts as the primary application service coordinating between
 * the domain model, scoring service, repository, and event publishing.
 * 
 * Requirements: 1.1, 2.2, 4.4, 6.1, 6.2, 6.3
 */
@Service
public class MatchDomainService implements MatchManagementPort, QueryPort {
    
    private final MatchRepositoryPort matchRepository;
    private final ScoringDomainService scoringService;
    private final EventPublisherPort eventPublisher;
    private final ValidationService validationService;
    
    /**
     * Constructor with dependency injection.
     * 依賴注入的建構子
     */
    public MatchDomainService(
            MatchRepositoryPort matchRepository,
            ScoringDomainService scoringService,
            EventPublisherPort eventPublisher,
            ValidationService validationService) {
        this.matchRepository = Objects.requireNonNull(matchRepository, "Match repository cannot be null");
        this.scoringService = Objects.requireNonNull(scoringService, "Scoring service cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
        this.validationService = Objects.requireNonNull(validationService, "Validation service cannot be null");
    }
    
    // MatchManagementPort implementation
    
    @Override
    public Match createMatch(String player1Name, String player2Name) {
        validationService.validatePlayerNames(player1Name, player2Name);
        
        // Create new match using factory method
        Match match = Match.create(player1Name, player2Name);
        
        // Save to repository
        Match savedMatch = matchRepository.save(match);
        
        // Publish match created event
        MatchCreatedEvent event = new MatchCreatedEvent(
            savedMatch.getMatchId(),
            savedMatch.getPlayer1().getName(),
            savedMatch.getPlayer2().getName()
        );
        eventPublisher.publishMatchCreated(event);
        
        return savedMatch;
    }
    
    @Override
    public Match scorePoint(String matchId, String playerId) {
        validationService.validateMatchId(matchId);
        validationService.validatePlayerId(playerId);
        
        // Retrieve match
        Match match = getMatchById(matchId);
        
        // Convert string playerId to PlayerId object
        PlayerId playerIdObj = PlayerId.of(playerId);
        
        // Score the point using scoring service
        boolean matchCompleted = scoringService.scorePoint(match, playerIdObj);
        
        // Save updated match
        Match updatedMatch = matchRepository.save(match);
        
        // Publish point scored event
        PointScoredEvent pointEvent = new PointScoredEvent(
            updatedMatch.getMatchId(),
            playerId,
            updatedMatch.getCurrentScore(),
            updatedMatch.getCurrentSetNumber(),
            updatedMatch.getCurrentGameNumber()
        );
        eventPublisher.publishPointScored(pointEvent);
        
        // Publish additional events based on game state
        publishGameStateEvents(updatedMatch, playerIdObj);
        
        // If match completed, publish match completed event
        if (matchCompleted) {
            MatchCompletedEvent completedEvent = new MatchCompletedEvent(
                updatedMatch.getMatchId(),
                updatedMatch.getWinner().getValue(),
                updatedMatch.getCurrentScore(),
                updatedMatch.getSets().size()
            );
            eventPublisher.publishMatchCompleted(completedEvent);
        }
        
        return updatedMatch;
    }
    
    @Override
    public void deleteMatch(String matchId) {
        validationService.validateMatchId(matchId);
        
        // Check if match exists
        if (!matchRepository.existsById(matchId)) {
            throw new MatchNotFoundException(matchId);
        }
        
        // Delete from repository
        matchRepository.deleteById(matchId);
        
        // Publish match deleted event
        eventPublisher.publishMatchDeleted(matchId, "system");
    }
    
    /**
     * Update an existing match.
     * 更新現有比賽
     * 
     * @param match the match to update
     * @return the updated match
     */
    public Match updateMatch(Match match) {
        Objects.requireNonNull(match, "Match cannot be null");
        validationService.validateMatchId(match.getMatchId());
        
        // Save updated match
        return matchRepository.save(match);
    }
    
    // QueryPort implementation
    
    @Override
    public Match getMatch(String matchId) {
        validationService.validateMatchId(matchId);
        return getMatchById(matchId);
    }
    
    @Override
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }
    
    @Override
    public List<Match> getMatchesByStatus(MatchStatus status) {
        validationService.validateMatchStatus(status);
        return matchRepository.findByStatus(status);
    }
    
    @Override
    public boolean matchExists(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            return false;
        }
        return matchRepository.existsById(matchId);
    }
    
    // Private helper methods
    
    /**
     * Retrieve match by ID with proper error handling.
     * 根據ID查詢比賽並進行適當的錯誤處理
     */
    private Match getMatchById(String matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
    }
    

    
    /**
     * Publish additional events based on game state changes.
     * 根據遊戲狀態變化發布額外事件
     */
    private void publishGameStateEvents(Match match, PlayerId playerId) {
        try {
            // Check if a game was completed
            Game currentGame = match.getCurrentGame();
            if (currentGame != null && currentGame.isCompleted()) {
                String winnerId = currentGame.getWinner().getValue();
                eventPublisher.publishGameCompleted(
                    match.getMatchId(), 
                    currentGame.getGameNumber(), 
                    winnerId
                );
            }
            
            // Check if a set was completed
            Set currentSet = match.getCurrentSet();
            if (currentSet != null && currentSet.isCompleted()) {
                String winnerId = currentSet.getWinner().getValue();
                eventPublisher.publishSetCompleted(
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
    
    // Additional business methods
    
    /**
     * Get the current score summary for a match.
     * 獲取比賽的當前比分摘要
     * 
     * @param matchId the match ID
     * @return formatted score string
     */
    public String getCurrentScore(String matchId) {
        validationService.validateMatchId(matchId);
        Match match = getMatchById(matchId);
        return scoringService.calculateCurrentScore(match);
    }
    
    /**
     * Check if a match is in deuce state.
     * 檢查比賽是否處於平分狀態
     * 
     * @param matchId the match ID
     * @return true if current game is in deuce
     */
    public boolean isMatchInDeuce(String matchId) {
        validationService.validateMatchId(matchId);
        Match match = getMatchById(matchId);
        return scoringService.isCurrentGameDeuce(match);
    }
    
    /**
     * Check if a player has advantage in the current game.
     * 檢查球員在當前局是否有優勢
     * 
     * @param matchId the match ID
     * @param playerId the player ID
     * @return true if player has advantage
     */
    public boolean playerHasAdvantage(String matchId, String playerId) {
        validationService.validateMatchId(matchId);
        validationService.validatePlayerId(playerId);
        
        Match match = getMatchById(matchId);
        PlayerId playerIdObj = PlayerId.of(playerId);
        
        return scoringService.hasAdvantage(match, playerIdObj);
    }
    
    /**
     * Check if the current game is a tiebreak.
     * 檢查當前局是否為搶七局
     * 
     * @param matchId the match ID
     * @return true if current game is a tiebreak
     */
    public boolean isCurrentGameTiebreak(String matchId) {
        validationService.validateMatchId(matchId);
        Match match = getMatchById(matchId);
        return scoringService.isCurrentGameTiebreak(match);
    }
    
    /**
     * Get match statistics summary.
     * 獲取比賽統計摘要
     * 
     * @param matchId the match ID
     * @return match statistics
     */
    public MatchStatistics getMatchStatistics(String matchId) {
        validationService.validateMatchId(matchId);
        Match match = getMatchById(matchId);
        
        return new MatchStatistics(
            match.getMatchId(),
            match.getPlayer1().getName(),
            match.getPlayer2().getName(),
            match.getPlayer1().getSetsWon(),
            match.getPlayer2().getSetsWon(),
            match.getPlayer1().getPointsWon(),
            match.getPlayer2().getPointsWon(),
            match.getCurrentSetNumber(),
            match.getCurrentGameNumber(),
            match.getStatus(),
            match.isCurrentGameTiebreak()
        );
    }
    
    /**
     * Cancel a match that is in progress.
     * 取消進行中的比賽
     * 
     * @param matchId the match ID
     * @return the cancelled match
     */
    public Match cancelMatch(String matchId) {
        validationService.validateMatchId(matchId);
        
        Match match = getMatchById(matchId);
        validationService.validateMatchStateForCancellation(match);
        
        // Cancel the match
        match.cancel();
        
        // Save updated match
        Match updatedMatch = matchRepository.save(match);
        
        // Publish match deleted event (cancelled matches are considered deleted)
        eventPublisher.publishMatchDeleted(matchId, "cancelled");
        
        return updatedMatch;
    }
    
    /**
     * Inner class for match statistics.
     * 比賽統計的內部類別
     */
    public static class MatchStatistics {
        private final String matchId;
        private final String player1Name;
        private final String player2Name;
        private final int player1Sets;
        private final int player2Sets;
        private final int player1Points;
        private final int player2Points;
        private final int currentSet;
        private final int currentGame;
        private final MatchStatus status;
        private final boolean isTiebreak;
        
        public MatchStatistics(String matchId, String player1Name, String player2Name,
                             int player1Sets, int player2Sets, int player1Points, int player2Points,
                             int currentSet, int currentGame, MatchStatus status, boolean isTiebreak) {
            this.matchId = matchId;
            this.player1Name = player1Name;
            this.player2Name = player2Name;
            this.player1Sets = player1Sets;
            this.player2Sets = player2Sets;
            this.player1Points = player1Points;
            this.player2Points = player2Points;
            this.currentSet = currentSet;
            this.currentGame = currentGame;
            this.status = status;
            this.isTiebreak = isTiebreak;
        }
        
        // Getters
        public String getMatchId() { return matchId; }
        public String getPlayer1Name() { return player1Name; }
        public String getPlayer2Name() { return player2Name; }
        public int getPlayer1Sets() { return player1Sets; }
        public int getPlayer2Sets() { return player2Sets; }
        public int getPlayer1Points() { return player1Points; }
        public int getPlayer2Points() { return player2Points; }
        public int getCurrentSet() { return currentSet; }
        public int getCurrentGame() { return currentGame; }
        public MatchStatus getStatus() { return status; }
        public boolean isTiebreak() { return isTiebreak; }
    }
}