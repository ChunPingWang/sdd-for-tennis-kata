package com.tennisscoring.domain.service;

import com.tennisscoring.domain.exception.InvalidMatchStateException;
import com.tennisscoring.domain.exception.MatchNotFoundException;
import com.tennisscoring.domain.factory.MatchFactoryRegistry;
import com.tennisscoring.domain.model.*;
import com.tennisscoring.ports.primary.*;
import com.tennisscoring.ports.secondary.MatchRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Domain service implementing match management and query operations.
 * 實作比賽管理和查詢操作的領域服務
 * 
 * This service follows the Single Responsibility Principle by focusing
 * solely on core match management operations. Statistics and event publishing
 * are handled by dedicated services.
 * 
 * Requirements: 1.1, 2.2, 4.4, 6.1, 6.2, 6.3
 */
@Service
public class MatchDomainService implements MatchService, MatchCreationPort, MatchScoringPort, MatchDeletionPort, MatchQueryPort {
    
    private final MatchRepositoryPort matchRepository;
    private final ScoringDomainService scoringService;
    private final MatchEventService eventService;
    private final MatchFactoryRegistry matchFactory;
    private final ValidationService validationService;
    
    /**
     * Constructor with dependency injection.
     * 依賴注入的建構子
     */
    public MatchDomainService(
            MatchRepositoryPort matchRepository,
            ScoringDomainService scoringService,
            MatchEventService eventService,
            MatchFactoryRegistry matchFactory,
            ValidationService validationService) {
        this.matchRepository = Objects.requireNonNull(matchRepository, "Match repository cannot be null");
        this.scoringService = Objects.requireNonNull(scoringService, "Scoring service cannot be null");
        this.eventService = Objects.requireNonNull(eventService, "Event service cannot be null");
        this.matchFactory = Objects.requireNonNull(matchFactory, "Match factory cannot be null");
        this.validationService = Objects.requireNonNull(validationService, "Validation service cannot be null");
    }
    
    // MatchManagementPort implementation
    
    @Override
    public Match createMatch(String player1Name, String player2Name) {
        return createMatch(null, player1Name, player2Name);
    }
    
    /**
     * Create a match with a specific type.
     * 創建特定類型的比賽
     * 
     * @param matchType the type of match to create (null for default)
     * @param player1Name name of the first player
     * @param player2Name name of the second player
     * @return the created match
     */
    public Match createMatch(String matchType, String player1Name, String player2Name) {
        validationService.validatePlayerNames(player1Name, player2Name);
        
        // Create new match using factory registry
        Match match = matchFactory.createMatch(matchType, player1Name, player2Name);
        
        // Save to repository
        Match savedMatch = matchRepository.save(match);
        
        // Publish match created event
        eventService.publishMatchCreated(savedMatch);
        
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
        eventService.publishPointScored(updatedMatch, playerId);
        
        // Publish additional events based on game state
        eventService.publishGameStateEvents(updatedMatch, playerIdObj);
        
        // If match completed, publish match completed event
        if (matchCompleted) {
            eventService.publishMatchCompleted(updatedMatch);
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
        eventService.publishMatchDeleted(matchId, "system");
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
        eventService.publishMatchDeleted(matchId, "cancelled");
        
        return updatedMatch;
    }
    
}