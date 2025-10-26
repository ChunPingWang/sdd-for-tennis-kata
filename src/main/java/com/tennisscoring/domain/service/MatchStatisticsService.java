package com.tennisscoring.domain.service;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;
import com.tennisscoring.domain.model.PlayerId;
import com.tennisscoring.ports.secondary.MatchRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service responsible for calculating and providing match statistics.
 * 負責計算和提供比賽統計資訊的服務
 * 
 * This service follows the Single Responsibility Principle by focusing
 * solely on statistics-related operations.
 * 
 * Requirements: 10.4
 */
@Service
public class MatchStatisticsService implements StatisticsService {
    
    private final MatchRepositoryPort matchRepository;
    private final ScoringDomainService scoringService;
    private final ValidationService validationService;
    
    public MatchStatisticsService(
            MatchRepositoryPort matchRepository,
            ScoringDomainService scoringService,
            ValidationService validationService) {
        this.matchRepository = Objects.requireNonNull(matchRepository, "Match repository cannot be null");
        this.scoringService = Objects.requireNonNull(scoringService, "Scoring service cannot be null");
        this.validationService = Objects.requireNonNull(validationService, "Validation service cannot be null");
    }
    
    /**
     * Get match statistics summary for a specific match.
     * 獲取特定比賽的統計摘要
     * 
     * @param matchId the match ID
     * @return match statistics
     */
    public MatchStatistics getMatchStatistics(String matchId) {
        validationService.validateMatchId(matchId);
        
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
        
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
     * Get system-wide match statistics.
     * 獲取系統範圍的比賽統計
     * 
     * @return system statistics
     */
    public SystemStatistics getSystemStatistics() {
        List<Match> allMatches = matchRepository.findAll();
        
        long inProgressCount = allMatches.stream()
                .filter(Match::isInProgress)
                .count();
        
        long completedCount = allMatches.stream()
                .filter(Match::isCompleted)
                .count();
        
        long cancelledCount = allMatches.stream()
                .filter(Match::isCancelled)
                .count();
        
        return new SystemStatistics(
                allMatches.size(),
                (int) inProgressCount,
                (int) completedCount,
                (int) cancelledCount
        );
    }
    
    /**
     * Get the current score summary for a match.
     * 獲取比賽的當前比分摘要
     * 
     * @param matchId the match ID
     * @return formatted score string
     */
    public String getCurrentScore(String matchId) {
        validationService.validateMatchId(matchId);
        
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
        
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
        
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
        
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
        
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
        
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
        
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
        
        return scoringService.isCurrentGameTiebreak(match);
    }
    
    /**
     * Match statistics data class.
     * 比賽統計資料類別
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
    
    /**
     * System statistics data class.
     * 系統統計資料類別
     */
    public static class SystemStatistics {
        private final int totalMatches;
        private final int inProgressMatches;
        private final int completedMatches;
        private final int cancelledMatches;
        
        public SystemStatistics(int totalMatches, int inProgressMatches, 
                              int completedMatches, int cancelledMatches) {
            this.totalMatches = totalMatches;
            this.inProgressMatches = inProgressMatches;
            this.completedMatches = completedMatches;
            this.cancelledMatches = cancelledMatches;
        }
        
        // Getters
        public int getTotalMatches() { return totalMatches; }
        public int getInProgressMatches() { return inProgressMatches; }
        public int getCompletedMatches() { return completedMatches; }
        public int getCancelledMatches() { return cancelledMatches; }
    }
}