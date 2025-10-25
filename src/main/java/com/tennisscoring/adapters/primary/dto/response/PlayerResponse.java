package com.tennisscoring.adapters.primary.dto.response;

/**
 * Response model for player information.
 * 球員資訊的回應模型
 */
public class PlayerResponse {
    
    private String playerId;
    private String name;
    private int setsWon;
    private int gamesWon;
    private int pointsWon;
    
    /**
     * Default constructor for JSON serialization.
     */
    public PlayerResponse() {
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param playerId player ID
     * @param name player name
     * @param setsWon number of sets won
     * @param gamesWon number of games won
     * @param pointsWon number of points won
     */
    public PlayerResponse(String playerId, String name, int setsWon, int gamesWon, int pointsWon) {
        this.playerId = playerId;
        this.name = name;
        this.setsWon = setsWon;
        this.gamesWon = gamesWon;
        this.pointsWon = pointsWon;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getSetsWon() {
        return setsWon;
    }
    
    public void setSetsWon(int setsWon) {
        this.setsWon = setsWon;
    }
    
    public int getGamesWon() {
        return gamesWon;
    }
    
    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }
    
    public int getPointsWon() {
        return pointsWon;
    }
    
    public void setPointsWon(int pointsWon) {
        this.pointsWon = pointsWon;
    }
    
    @Override
    public String toString() {
        return "PlayerResponse{" +
                "playerId='" + playerId + '\'' +
                ", name='" + name + '\'' +
                ", setsWon=" + setsWon +
                ", gamesWon=" + gamesWon +
                ", pointsWon=" + pointsWon +
                '}';
    }
}