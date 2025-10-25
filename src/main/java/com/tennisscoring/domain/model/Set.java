package com.tennisscoring.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Set entity representing a tennis set within a match.
 * Manages games and determines set winners based on tennis rules.
 */
public class Set {
    
    private final int setNumber;
    private final List<Game> games;
    private final Map<PlayerId, Integer> gamesWon;
    private boolean isCompleted;
    private PlayerId winner;
    private PlayerId player1Id;
    private PlayerId player2Id;
    
    /**
     * Constructor for creating a new set.
     * @param setNumber the sequential number of this set in the match
     */
    public Set(int setNumber) {
        this.setNumber = setNumber;
        this.games = new ArrayList<>();
        this.gamesWon = new HashMap<>();
        this.isCompleted = false;
        this.winner = null;
    }
    
    /**
     * Initialize the set with player IDs and create the first game.
     * @param player1Id ID of player 1
     * @param player2Id ID of player 2
     */
    public void initialize(PlayerId player1Id, PlayerId player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.gamesWon.put(player1Id, 0);
        this.gamesWon.put(player2Id, 0);
        
        // Create the first game
        addNewGame(false);
    }
    
    /**
     * Add a new game to this set.
     * @param isTiebreak whether this should be a tiebreak game
     */
    public void addNewGame(boolean isTiebreak) {
        if (isCompleted) {
            throw new IllegalStateException("Cannot add game to completed set");
        }
        
        int gameNumber = games.size() + 1;
        Game newGame = new Game(gameNumber, isTiebreak);
        newGame.initializeScores(player1Id, player2Id);
        games.add(newGame);
    }
    
    /**
     * Get the current active game.
     * @return the current game, or null if set is completed
     */
    public Game getCurrentGame() {
        if (isCompleted) {
            return null;
        }
        
        return games.stream()
                .filter(game -> !game.isCompleted())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Handle completion of a game and update set state.
     * @param gameWinner the player who won the game
     * @return true if the set is completed after this game
     */
    public boolean completeGame(PlayerId gameWinner) {
        // Increment games won for the winner
        int currentGamesWon = gamesWon.get(gameWinner);
        gamesWon.put(gameWinner, currentGamesWon + 1);
        
        // Check if set is won
        if (isSetWon(gameWinner)) {
            completeSet(gameWinner);
            return true;
        }
        
        // Check if tiebreak should be played
        if (shouldStartTiebreak()) {
            addNewGame(true);
        } else {
            addNewGame(false);
        }
        
        return false;
    }
    
    /**
     * Check if a player has won the set.
     * @param playerId the player to check
     * @return true if the player has won the set
     */
    private boolean isSetWon(PlayerId playerId) {
        int playerGames = gamesWon.get(playerId);
        PlayerId opponentId = getOpponentId(playerId);
        int opponentGames = gamesWon.get(opponentId);
        
        // Standard set win: 6 games with at least 2-game lead
        if (playerGames >= 6 && playerGames - opponentGames >= 2) {
            return true;
        }
        
        // Tiebreak win: 7-6 after tiebreak
        if (playerGames == 7 && opponentGames == 6) {
            Game lastGame = games.get(games.size() - 1);
            return lastGame.isTiebreak() && lastGame.getWinner().equals(playerId);
        }
        
        return false;
    }
    
    /**
     * Check if a tiebreak should be started.
     * @return true if both players have 6 games
     */
    public boolean shouldStartTiebreak() {
        return gamesWon.get(player1Id) == 6 && gamesWon.get(player2Id) == 6;
    }
    
    /**
     * Mark the set as completed with a winner.
     * @param winnerId the ID of the set winner
     */
    private void completeSet(PlayerId winnerId) {
        this.isCompleted = true;
        this.winner = winnerId;
    }
    
    /**
     * Get the opponent's ID.
     * @param playerId the player's ID
     * @return the opponent's ID
     */
    private PlayerId getOpponentId(PlayerId playerId) {
        if (playerId.equals(player1Id)) {
            return player2Id;
        } else if (playerId.equals(player2Id)) {
            return player1Id;
        } else {
            throw new IllegalArgumentException("Player not found in this set: " + playerId);
        }
    }
    
    /**
     * Get the number of games won by a player.
     * @param playerId the player's ID
     * @return the number of games won
     */
    public int getGamesWon(PlayerId playerId) {
        return gamesWon.getOrDefault(playerId, 0);
    }
    
    /**
     * Get a formatted score string for this set.
     * @return formatted score string (e.g., "6-4", "7-6")
     */
    public String getFormattedScore() {
        if (player1Id == null || player2Id == null) {
            return "0-0";
        }
        
        int player1Games = getGamesWon(player1Id);
        int player2Games = getGamesWon(player2Id);
        
        return player1Games + "-" + player2Games;
    }
    
    /**
     * Get the current game score if set is in progress.
     * @return formatted current game score, or empty string if set completed
     */
    public String getCurrentGameScore() {
        if (isCompleted) {
            return "";
        }
        
        Game currentGame = getCurrentGame();
        if (currentGame == null) {
            return "";
        }
        
        return currentGame.getFormattedScore(player1Id, player2Id);
    }
    
    /**
     * Check if this set is currently in a tiebreak.
     * @return true if the current game is a tiebreak
     */
    public boolean isInTiebreak() {
        if (isCompleted) {
            return false;
        }
        
        Game currentGame = getCurrentGame();
        return currentGame != null && currentGame.isTiebreak();
    }
    
    // Getters
    public int getSetNumber() {
        return setNumber;
    }
    
    public List<Game> getGames() {
        return Collections.unmodifiableList(games);
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public PlayerId getWinner() {
        return winner;
    }
    
    public PlayerId getPlayer1Id() {
        return player1Id;
    }
    
    public PlayerId getPlayer2Id() {
        return player2Id;
    }
    
    /**
     * Get the total number of games played in this set.
     * @return total games played
     */
    public int getTotalGamesPlayed() {
        return games.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Set set = (Set) o;
        return setNumber == set.setNumber;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(setNumber);
    }
    
    @Override
    public String toString() {
        return "Set{" +
                "setNumber=" + setNumber +
                ", gamesWon=" + gamesWon +
                ", isCompleted=" + isCompleted +
                ", winner=" + winner +
                ", totalGames=" + games.size() +
                '}';
    }
}