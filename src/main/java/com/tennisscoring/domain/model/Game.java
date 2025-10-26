package com.tennisscoring.domain.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Game entity representing a single game within a tennis set.
 * Handles both regular scoring (15-30-40) and tiebreak scoring (1-2-3...).
 */
public class Game {
    
    private final int gameNumber;
    private final Map<PlayerId, GameScore> scores;
    private final Map<PlayerId, Integer> tiebreakScores;
    private final boolean isTiebreak;
    private GameStatus status;
    private PlayerId winner;
    
    /**
     * Constructor for creating a new game.
     * @param gameNumber the sequential number of this game in the set
     * @param isTiebreak whether this is a tiebreak game
     */
    public Game(int gameNumber, boolean isTiebreak) {
        this.gameNumber = gameNumber;
        this.isTiebreak = isTiebreak;
        this.scores = new HashMap<>();
        this.tiebreakScores = new HashMap<>();
        this.status = GameStatus.IN_PROGRESS;
        this.winner = null;
    }
    
    /**
     * Initialize scores for both players.
     * @param player1Id ID of player 1
     * @param player2Id ID of player 2
     */
    public void initializeScores(PlayerId player1Id, PlayerId player2Id) {
        if (isTiebreak) {
            tiebreakScores.put(player1Id, 0);
            tiebreakScores.put(player2Id, 0);
        } else {
            scores.put(player1Id, GameScore.LOVE);
            scores.put(player2Id, GameScore.LOVE);
        }
    }
    
    /**
     * Record a point scored by a player.
     * @param playerId the ID of the player who scored
     * @param opponentId the ID of the opponent
     * @return true if the game is completed after this point
     */
    public boolean scorePoint(PlayerId playerId, PlayerId opponentId) {
        if (isCompleted()) {
            throw new IllegalStateException("Cannot score on completed game");
        }
        
        if (isTiebreak) {
            return scoreTiebreakPoint(playerId, opponentId);
        } else {
            return scoreRegularPoint(playerId, opponentId);
        }
    }
    
    /**
     * Handle scoring for regular games (15-30-40).
     */
    private boolean scoreRegularPoint(PlayerId playerId, PlayerId opponentId) {
        GameScore currentScore = scores.get(playerId);
        GameScore opponentScore = scores.get(opponentId);
        
        switch (currentScore) {
            case LOVE -> scores.put(playerId, GameScore.FIFTEEN);
            case FIFTEEN -> scores.put(playerId, GameScore.THIRTY);
            case THIRTY -> scores.put(playerId, GameScore.FORTY);
            case FORTY -> {
                return handleFortyScoring(playerId, opponentId, opponentScore);
            }
            case ADVANTAGE -> {
                return winGame(playerId);
            }
        }
        
        return false;
    }
    
    /**
     * Handle scoring when player has 40 points.
     */
    private boolean handleFortyScoring(PlayerId playerId, PlayerId opponentId, GameScore opponentScore) {
        switch (opponentScore) {
            case LOVE, FIFTEEN, THIRTY -> {
                return winGame(playerId);
            }
            case FORTY -> {
                // Enter deuce/advantage state
                status = GameStatus.DEUCE;
                scores.put(playerId, GameScore.ADVANTAGE);
                return false;
            }
            case ADVANTAGE -> {
                // Back to deuce
                status = GameStatus.DEUCE;
                scores.put(opponentId, GameScore.FORTY);
                scores.put(playerId, GameScore.FORTY);
                return false;
            }
        }
        return false;
    }
    
    /**
     * Handle scoring for tiebreak games.
     */
    private boolean scoreTiebreakPoint(PlayerId playerId, PlayerId opponentId) {
        int currentPoints = tiebreakScores.get(playerId);
        int opponentPoints = tiebreakScores.get(opponentId);
        
        currentPoints++;
        tiebreakScores.put(playerId, currentPoints);
        
        // Win condition: reach 7 points with at least 2-point lead
        if (currentPoints >= 7 && currentPoints - opponentPoints >= 2) {
            return winGame(playerId);
        }
        
        return false;
    }
    
    /**
     * Mark the game as won by the specified player.
     */
    private boolean winGame(PlayerId playerId) {
        this.status = GameStatus.COMPLETED;
        this.winner = playerId;
        return true;
    }
    
    /**
     * Get the current score for a player in regular games.
     * @param playerId the player's ID
     * @return the player's current GameScore
     */
    public GameScore getScore(PlayerId playerId) {
        if (isTiebreak) {
            throw new IllegalStateException("Use getTiebreakScore for tiebreak games");
        }
        return scores.get(playerId);
    }
    
    /**
     * Get all player scores (for mapper compatibility).
     * @return map of player IDs to scores
     */
    public Map<PlayerId, GameScore> getPlayerScores() {
        if (isTiebreak) {
            throw new IllegalStateException("Use getTiebreakScores for tiebreak games");
        }
        return Collections.unmodifiableMap(scores);
    }
    
    /**
     * Get player score (alias for getScore for mapper compatibility).
     * @param playerId the player's ID
     * @return the player's current GameScore
     */
    public GameScore getPlayerScore(PlayerId playerId) {
        return getScore(playerId);
    }
    
    /**
     * Get the current score for a player in tiebreak games.
     * @param playerId the player's ID
     * @return the player's current tiebreak score
     */
    public int getTiebreakScore(PlayerId playerId) {
        if (!isTiebreak) {
            throw new IllegalStateException("Use getScore for regular games");
        }
        return tiebreakScores.getOrDefault(playerId, 0);
    }
    
    /**
     * Get a formatted score string for display.
     * @param player1Id ID of player 1
     * @param player2Id ID of player 2
     * @return formatted score string
     */
    public String getFormattedScore(PlayerId player1Id, PlayerId player2Id) {
        if (isTiebreak) {
            return getTiebreakScore(player1Id) + "-" + getTiebreakScore(player2Id);
        } else {
            GameScore p1Score = getScore(player1Id);
            GameScore p2Score = getScore(player2Id);
            
            // Handle deuce and advantage display
            if (p1Score == GameScore.FORTY && p2Score == GameScore.FORTY) {
                if (status == GameStatus.DEUCE) {
                    return "平分";
                } else {
                    return "40-40";
                }
            } else if (p1Score == GameScore.ADVANTAGE) {
                return "AD-40";
            } else if (p2Score == GameScore.ADVANTAGE) {
                return "40-AD";
            }
            
            return p1Score.getDisplayValue() + "-" + p2Score.getDisplayValue();
        }
    }
    
    /**
     * Check if this game is in deuce state.
     * @return true if both players have 40 and no advantage
     */
    public boolean isDeuce() {
        return status == GameStatus.DEUCE && 
               scores.values().stream().allMatch(score -> score == GameScore.FORTY);
    }
    
    /**
     * Check if a player has advantage.
     * @param playerId the player to check
     * @return true if the player has advantage
     */
    public boolean hasAdvantage(PlayerId playerId) {
        return scores.get(playerId) == GameScore.ADVANTAGE;
    }
    
    // Getters
    public int getGameNumber() {
        return gameNumber;
    }
    
    public boolean isTiebreak() {
        return isTiebreak;
    }
    
    public GameStatus getStatus() {
        return status;
    }
    
    public boolean isCompleted() {
        return status == GameStatus.COMPLETED;
    }
    
    public PlayerId getWinner() {
        return winner;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameNumber == game.gameNumber && isTiebreak == game.isTiebreak;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(gameNumber, isTiebreak);
    }
    
    @Override
    public String toString() {
        return "Game{" +
                "gameNumber=" + gameNumber +
                ", isTiebreak=" + isTiebreak +
                ", status=" + status +
                ", winner=" + winner +
                '}';
    }
}