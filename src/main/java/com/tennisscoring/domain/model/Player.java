package com.tennisscoring.domain.model;

import java.util.Objects;

/**
 * Player entity representing a tennis player in a match.
 * Contains player identification, name, and match statistics.
 */
public class Player {
    
    private final PlayerId playerId;
    private final PlayerName name;
    private int setsWon;
    private int gamesWon;
    private int pointsWon;
    
    /**
     * Constructor for creating a new Player.
     * @param playerId unique identifier for the player
     * @param name the player's name
     */
    public Player(PlayerId playerId, PlayerName name) {
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.name = Objects.requireNonNull(name, "Player name cannot be null");
        this.setsWon = 0;
        this.gamesWon = 0;
        this.pointsWon = 0;
    }
    
    /**
     * Factory method to create a Player with string name.
     * @param playerId unique identifier for the player
     * @param name the player's name as string
     * @return new Player instance
     */
    public static Player create(PlayerId playerId, String name) {
        return new Player(playerId, PlayerName.of(name));
    }
    
    /**
     * Factory method to create a Player with generated ID.
     * @param name the player's name as string
     * @return new Player instance with generated ID
     */
    public static Player create(String name) {
        return new Player(PlayerId.generate(), PlayerName.of(name));
    }
    
    /**
     * Increment the number of sets won by this player.
     */
    public void incrementSetsWon() {
        this.setsWon++;
    }
    
    /**
     * Increment the number of games won by this player.
     */
    public void incrementGamesWon() {
        this.gamesWon++;
    }
    
    /**
     * Increment the number of points won by this player.
     */
    public void incrementPointsWon() {
        this.pointsWon++;
    }
    
    /**
     * Reset game statistics (used when starting a new set).
     */
    public void resetGameStats() {
        this.gamesWon = 0;
    }
    
    /**
     * Reset all statistics (used when starting a new match).
     */
    public void resetAllStats() {
        this.setsWon = 0;
        this.gamesWon = 0;
        this.pointsWon = 0;
    }
    
    /**
     * Check if this player has won the match (2 sets in best of 3).
     * @return true if player has won 2 sets
     */
    public boolean hasWonMatch() {
        return setsWon >= 2;
    }
    
    /**
     * Check if this player has won the current set.
     * @param opponentGamesWon number of games won by opponent
     * @return true if player has won the set
     */
    public boolean hasWonSet(int opponentGamesWon) {
        return gamesWon >= 6 && (gamesWon - opponentGamesWon) >= 2;
    }
    
    /**
     * Check if a tiebreak should be played.
     * @param opponentGamesWon number of games won by opponent
     * @return true if both players have 6 games
     */
    public boolean shouldPlayTiebreak(int opponentGamesWon) {
        return gamesWon == 6 && opponentGamesWon == 6;
    }
    
    // Getters
    public PlayerId getPlayerId() {
        return playerId;
    }
    
    public String getName() {
        return name.getValue();
    }
    
    public PlayerName getPlayerName() {
        return name;
    }
    
    public int getSetsWon() {
        return setsWon;
    }
    
    public int getGamesWon() {
        return gamesWon;
    }
    
    public int getPointsWon() {
        return pointsWon;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(playerId, player.playerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
    
    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", name=" + name +
                ", setsWon=" + setsWon +
                ", gamesWon=" + gamesWon +
                ", pointsWon=" + pointsWon +
                '}';
    }
}