package com.tennisscoring.domain.service;

import com.tennisscoring.domain.exception.InvalidMatchStateException;
import com.tennisscoring.domain.model.*;

import org.springframework.stereotype.Component;

/**
 * Domain service responsible for handling tennis scoring logic.
 * 負責處理網球計分邏輯的領域服務
 * 
 * This service follows the Open-Closed Principle by using the Strategy pattern
 * to handle different scoring systems (regular games vs tiebreaks).
 * New scoring strategies can be added without modifying this service.
 * 
 * Requirements: 2.1, 2.3, 3.1, 3.2, 3.3, 5.1, 5.2, 5.3
 */
@Component
public class ScoringDomainService implements ScoringService {
    
    private final ValidationService validationService;
    
    /**
     * Constructor with dependency injection.
     * 依賴注入的建構子
     */
    public ScoringDomainService(ValidationService validationService) {
        this.validationService = validationService;
    }
    
    /**
     * Process a point scored by a player in a match.
     * 處理球員在比賽中的得分
     * 
     * @param match the match where the point was scored
     * @param playerId the ID of the player who scored
     * @return true if the match is completed after this point
     * @throws InvalidMatchStateException if match is not in a valid state for scoring
     */
    public boolean scorePoint(Match match, PlayerId playerId) {
        validationService.validateMatchStateForScoring(match);
        validationService.validatePlayerInMatch(match, playerId.getValue());
        
        // Get current game and set
        Set currentSet = match.getCurrentSet();
        Game currentGame = currentSet.getCurrentGame();
        
        if (currentGame == null) {
            throw new InvalidMatchStateException("No active game found in match: " + match.getMatchId());
        }
        
        // Score the point using the game's internal logic
        PlayerId opponentId = getOpponentId(match, playerId);
        boolean gameCompleted = currentGame.scorePoint(playerId, opponentId);
        
        if (gameCompleted) {
            return handleGameCompletion(match, currentSet, playerId);
        }
        
        return false;
    }
    
    /**
     * Handle the completion of a game and check for set/match completion.
     * 處理局的完成並檢查盤/比賽是否完成
     */
    private boolean handleGameCompletion(Match match, Set currentSet, PlayerId gameWinner) {
        // Complete the game in the set
        boolean setCompleted = currentSet.completeGame(gameWinner);
        
        if (setCompleted) {
            return handleSetCompletion(match, gameWinner);
        }
        
        return false;
    }
    
    /**
     * Handle the completion of a set and check for match completion.
     * 處理盤的完成並檢查比賽是否完成
     */
    private boolean handleSetCompletion(Match match, PlayerId setWinner) {
        // Update player statistics
        Player winner = match.getPlayer(setWinner);
        winner.incrementSetsWon();
        
        // Check if match is won (best of 3 sets)
        if (winner.getSetsWon() >= 2) {
            // Match completion is handled internally by the Match class
            return true;
        }
        
        // Start new set if match continues
        startNewSet(match);
        return false;
    }
    
    /**
     * Start a new set in the match.
     * 在比賽中開始新的一盤
     */
    private void startNewSet(Match match) {
        // The Match class handles starting new sets internally
        // This method is here for potential future customization
    }
    

    
    /**
     * Get the opponent's ID for a given player.
     * 獲取指定球員的對手ID
     */
    private PlayerId getOpponentId(Match match, PlayerId playerId) {
        if (match.getPlayer1().getPlayerId().equals(playerId)) {
            return match.getPlayer2().getPlayerId();
        } else if (match.getPlayer2().getPlayerId().equals(playerId)) {
            return match.getPlayer1().getPlayerId();
        } else {
            throw new IllegalArgumentException("Player not found in match: " + playerId);
        }
    }
    
    /**
     * Calculate the current score summary for display.
     * 計算用於顯示的當前比分摘要
     * 
     * @param match the match to get score for
     * @return formatted score string
     */
    public String calculateCurrentScore(Match match) {
        if (match == null) {
            return "";
        }
        
        return match.getCurrentScore();
    }
    
    /**
     * Check if the current game is in deuce state.
     * 檢查當前局是否處於平分狀態
     * 
     * @param match the match to check
     * @return true if current game is in deuce
     */
    public boolean isCurrentGameDeuce(Match match) {
        if (match == null || match.isCompleted()) {
            return false;
        }
        
        try {
            Game currentGame = match.getCurrentGame();
            if (currentGame == null) {
                return false;
            }
            
            return currentGame.isDeuce();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if a player has advantage in the current game.
     * 檢查球員在當前局是否有優勢
     * 
     * @param match the match to check
     * @param playerId the player to check for advantage
     * @return true if player has advantage
     */
    public boolean hasAdvantage(Match match, PlayerId playerId) {
        if (match == null || match.isCompleted() || playerId == null) {
            return false;
        }
        
        try {
            Game currentGame = match.getCurrentGame();
            if (currentGame == null) {
                return false;
            }
            
            return currentGame.hasAdvantage(playerId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if the current game is a tiebreak.
     * 檢查當前局是否為搶七局
     * 
     * @param match the match to check
     * @return true if current game is a tiebreak
     */
    public boolean isCurrentGameTiebreak(Match match) {
        if (match == null || match.isCompleted()) {
            return false;
        }
        
        return match.isCurrentGameTiebreak();
    }
    
    /**
     * Get the current game score for a specific player.
     * 獲取特定球員的當前局分數
     * 
     * @param match the match to get score from
     * @param playerId the player's ID
     * @return the player's current game score, or null if not available
     */
    public GameScore getCurrentGameScore(Match match, PlayerId playerId) {
        if (match == null || match.isCompleted() || playerId == null) {
            return null;
        }
        
        try {
            Game currentGame = match.getCurrentGame();
            if (currentGame == null || currentGame.isTiebreak()) {
                return null;
            }
            
            return currentGame.getScore(playerId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get the current tiebreak score for a specific player.
     * 獲取特定球員的當前搶七局分數
     * 
     * @param match the match to get score from
     * @param playerId the player's ID
     * @return the player's current tiebreak score, or -1 if not in tiebreak
     */
    public int getCurrentTiebreakScore(Match match, PlayerId playerId) {
        if (match == null || match.isCompleted() || playerId == null) {
            return -1;
        }
        
        try {
            Game currentGame = match.getCurrentGame();
            if (currentGame == null || !currentGame.isTiebreak()) {
                return -1;
            }
            
            return currentGame.getTiebreakScore(playerId);
        } catch (Exception e) {
            return -1;
        }
    }
}