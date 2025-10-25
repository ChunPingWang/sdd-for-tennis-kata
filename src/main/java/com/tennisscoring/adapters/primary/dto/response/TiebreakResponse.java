package com.tennisscoring.adapters.primary.dto.response;

/**
 * Response model for tiebreak information.
 * 搶七局資訊的回應模型
 */
public class TiebreakResponse {
    
    private int player1Points;
    private int player2Points;
    private boolean isCompleted;
    private String winnerId;
    
    /**
     * Default constructor for JSON serialization.
     */
    public TiebreakResponse() {
    }
    
    /**
     * Constructor with tiebreak information.
     * 
     * @param player1Points player 1's tiebreak points
     * @param player2Points player 2's tiebreak points
     * @param isCompleted whether the tiebreak is completed
     * @param winnerId the winner's ID if completed
     */
    public TiebreakResponse(int player1Points, int player2Points, boolean isCompleted, String winnerId) {
        this.player1Points = player1Points;
        this.player2Points = player2Points;
        this.isCompleted = isCompleted;
        this.winnerId = winnerId;
    }
    
    public int getPlayer1Points() {
        return player1Points;
    }
    
    public void setPlayer1Points(int player1Points) {
        this.player1Points = player1Points;
    }
    
    public int getPlayer2Points() {
        return player2Points;
    }
    
    public void setPlayer2Points(int player2Points) {
        this.player2Points = player2Points;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public String getWinnerId() {
        return winnerId;
    }
    
    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }
    
    @Override
    public String toString() {
        return "TiebreakResponse{" +
                "player1Points=" + player1Points +
                ", player2Points=" + player2Points +
                ", isCompleted=" + isCompleted +
                ", winnerId='" + winnerId + '\'' +
                '}';
    }
}