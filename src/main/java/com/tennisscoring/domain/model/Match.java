package com.tennisscoring.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Match aggregate root representing a complete tennis match.
 * Manages sets, players, and overall match state.
 */
public class Match {
    
    private final MatchId matchId;
    private final Player player1;
    private final Player player2;
    private final List<Set> sets;
    private MatchStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private PlayerId winner;
    
    /**
     * Private constructor to enforce factory method usage.
     * @param matchId unique identifier for the match
     * @param player1 first player
     * @param player2 second player
     */
    private Match(MatchId matchId, Player player1, Player player2) {
        this.matchId = Objects.requireNonNull(matchId, "Match ID cannot be null");
        this.player1 = Objects.requireNonNull(player1, "Player 1 cannot be null");
        this.player2 = Objects.requireNonNull(player2, "Player 2 cannot be null");
        this.sets = new ArrayList<>();
        this.status = MatchStatus.IN_PROGRESS;
        this.createdAt = LocalDateTime.now();
        this.completedAt = null;
        this.winner = null;
        
        // Initialize the first set
        initializeFirstSet();
    }
    
    /**
     * Factory method to create a new match.
     * @param player1Name name of the first player
     * @param player2Name name of the second player
     * @return new Match instance
     */
    public static Match create(String player1Name, String player2Name) {
        MatchId matchId = MatchId.generate();
        Player player1 = Player.create(player1Name);
        Player player2 = Player.create(player2Name);
        
        return new Match(matchId, player1, player2);
    }
    
    /**
     * Factory method to create a match with specific IDs.
     * @param matchId the match identifier
     * @param player1 first player
     * @param player2 second player
     * @return new Match instance
     */
    public static Match create(MatchId matchId, Player player1, Player player2) {
        return new Match(matchId, player1, player2);
    }
    
    /**
     * Initialize the first set of the match.
     */
    private void initializeFirstSet() {
        Set firstSet = new Set(1);
        firstSet.initialize(player1.getPlayerId(), player2.getPlayerId());
        sets.add(firstSet);
    }
    
    /**
     * Record a point scored by a player.
     * @param playerId the ID of the player who scored
     * @return true if the match is completed after this point
     */
    public boolean scorePoint(PlayerId playerId) {
        if (isCompleted()) {
            throw new IllegalStateException("Cannot score on completed match");
        }
        
        validatePlayer(playerId);
        
        // Get current set and game
        Set currentSet = getCurrentSet();
        Game currentGame = currentSet.getCurrentGame();
        
        if (currentGame == null) {
            throw new IllegalStateException("No active game found");
        }
        
        // Score the point
        PlayerId opponentId = getOpponentId(playerId);
        boolean gameCompleted = currentGame.scorePoint(playerId, opponentId);
        
        if (gameCompleted) {
            // Handle game completion
            boolean setCompleted = currentSet.completeGame(playerId);
            
            if (setCompleted) {
                // Handle set completion
                getPlayer(playerId).incrementSetsWon();
                
                if (isMatchWon(playerId)) {
                    completeMatch(playerId);
                    return true;
                } else {
                    // Start new set
                    startNewSet();
                }
            }
        }
        
        // Update player statistics
        getPlayer(playerId).incrementPointsWon();
        
        return false;
    }
    
    /**
     * Start a new set.
     */
    private void startNewSet() {
        int nextSetNumber = sets.size() + 1;
        Set newSet = new Set(nextSetNumber);
        newSet.initialize(player1.getPlayerId(), player2.getPlayerId());
        sets.add(newSet);
        
        // Reset game statistics for both players
        player1.resetGameStats();
        player2.resetGameStats();
    }
    
    /**
     * Check if a player has won the match.
     * @param playerId the player to check
     * @return true if the player has won 2 sets
     */
    private boolean isMatchWon(PlayerId playerId) {
        return getPlayer(playerId).getSetsWon() >= 2;
    }
    
    /**
     * Complete the match with a winner.
     * @param winnerId the ID of the match winner
     */
    private void completeMatch(PlayerId winnerId) {
        this.status = MatchStatus.COMPLETED;
        this.winner = winnerId;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Cancel the match.
     */
    public void cancel() {
        if (isCompleted()) {
            throw new IllegalStateException("Cannot cancel completed match");
        }
        
        this.status = MatchStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Get the current active set.
     * @return the current set
     */
    public Set getCurrentSet() {
        return sets.stream()
                .filter(set -> !set.isCompleted())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active set found"));
    }
    
    /**
     * Get the current active game.
     * @return the current game
     */
    public Game getCurrentGame() {
        Set currentSet = getCurrentSet();
        return currentSet.getCurrentGame();
    }
    
    /**
     * Get a player by ID.
     * @param playerId the player's ID
     * @return the Player object
     */
    public Player getPlayer(PlayerId playerId) {
        if (player1.getPlayerId().equals(playerId)) {
            return player1;
        } else if (player2.getPlayerId().equals(playerId)) {
            return player2;
        }
        throw new IllegalArgumentException("Player not found: " + playerId);
    }
    
    /**
     * Get a player by string ID.
     * @param playerId the player's ID as string
     * @return the Player object
     */
    public Player getPlayer(String playerId) {
        return getPlayer(PlayerId.of(playerId));
    }
    
    /**
     * Get the opponent's ID.
     * @param playerId the player's ID
     * @return the opponent's ID
     */
    private PlayerId getOpponentId(PlayerId playerId) {
        if (player1.getPlayerId().equals(playerId)) {
            return player2.getPlayerId();
        } else if (player2.getPlayerId().equals(playerId)) {
            return player1.getPlayerId();
        }
        throw new IllegalArgumentException("Player not found: " + playerId);
    }
    
    /**
     * Validate that the player ID belongs to this match.
     * @param playerId the player ID to validate
     */
    private void validatePlayer(PlayerId playerId) {
        if (!player1.getPlayerId().equals(playerId) && !player2.getPlayerId().equals(playerId)) {
            throw new IllegalArgumentException("Player not found in this match: " + playerId);
        }
    }
    
    /**
     * Get the current score summary.
     * @return formatted score string
     */
    public String getCurrentScore() {
        StringBuilder score = new StringBuilder();
        
        // Add set scores
        for (Set set : sets) {
            if (set.isCompleted()) {
                score.append(set.getFormattedScore()).append(" ");
            } else {
                score.append(set.getFormattedScore());
                
                // Add current game score if set is in progress
                String gameScore = set.getCurrentGameScore();
                if (!gameScore.isEmpty()) {
                    score.append(" (").append(gameScore).append(")");
                }
            }
        }
        
        return score.toString().trim();
    }
    
    /**
     * Get the match winner.
     * @return the winning Player, or null if match not completed
     */
    public Player getWinnerPlayer() {
        if (winner == null) {
            return null;
        }
        return getPlayer(winner);
    }
    
    // Getters
    public String getMatchId() {
        return matchId.getValue();
    }
    
    public MatchId getMatchIdObject() {
        return matchId;
    }
    
    public Player getPlayer1() {
        return player1;
    }
    
    public Player getPlayer2() {
        return player2;
    }
    
    public List<Set> getSets() {
        return Collections.unmodifiableList(sets);
    }
    
    public MatchStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public PlayerId getWinner() {
        return winner;
    }
    
    public boolean isCompleted() {
        return status == MatchStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == MatchStatus.CANCELLED;
    }
    
    public boolean isInProgress() {
        return status == MatchStatus.IN_PROGRESS;
    }
    
    /**
     * Get the current set number.
     * @return the number of the current set
     */
    public int getCurrentSetNumber() {
        return getCurrentSet().getSetNumber();
    }
    
    /**
     * Get the current game number within the current set.
     * @return the number of the current game
     */
    public int getCurrentGameNumber() {
        Game currentGame = getCurrentGame();
        return currentGame != null ? currentGame.getGameNumber() : 0;
    }
    
    /**
     * Check if the current game is a tiebreak.
     * @return true if current game is a tiebreak
     */
    public boolean isCurrentGameTiebreak() {
        Game currentGame = getCurrentGame();
        return currentGame != null && currentGame.isTiebreak();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(matchId, match.matchId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(matchId);
    }
    
    @Override
    public String toString() {
        return "Match{" +
                "matchId=" + matchId +
                ", player1=" + player1.getName() +
                ", player2=" + player2.getName() +
                ", status=" + status +
                ", currentScore='" + getCurrentScore() + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}