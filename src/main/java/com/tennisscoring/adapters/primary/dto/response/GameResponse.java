package com.tennisscoring.adapters.primary.dto.response;

/**
 * Response model for game information.
 * 局資訊的回應模型
 */
public class GameResponse {
    
    private int gameNumber;
    private String player1Score;
    private String player2Score;
    private boolean isCompleted;
    private String winnerId;
    private boolean isTiebreak;
    private String status;
    
    /**
     * Default constructor for JSON serialization.
     */
    public GameResponse() {
    }
    
    /**
     * Constructor with basic game information.
     * 
     * @param gameNumber the game number
     * @param player1Score player 1's score
     * @param player2Score player 2's score
     * @param isCompleted whether the game is completed
     */
    public GameResponse(int gameNumber, String player1Score, String player2Score, boolean isCompleted) {
        this.gameNumber = gameNumber;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.isCompleted = isCompleted;
    }
    
    public int getGameNumber() {
        return gameNumber;
    }
    
    public void setGameNumber(int gameNumber) {
        this.gameNumber = gameNumber;
    }
    
    public String getPlayer1Score() {
        return player1Score;
    }
    
    public void setPlayer1Score(String player1Score) {
        this.player1Score = player1Score;
    }
    
    public String getPlayer2Score() {
        return player2Score;
    }
    
    public void setPlayer2Score(String player2Score) {
        this.player2Score = player2Score;
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
    
    public boolean isTiebreak() {
        return isTiebreak;
    }
    
    public void setTiebreak(boolean tiebreak) {
        isTiebreak = tiebreak;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "GameResponse{" +
                "gameNumber=" + gameNumber +
                ", player1Score='" + player1Score + '\'' +
                ", player2Score='" + player2Score + '\'' +
                ", isCompleted=" + isCompleted +
                ", winnerId='" + winnerId + '\'' +
                ", isTiebreak=" + isTiebreak +
                ", status='" + status + '\'' +
                '}';
    }
}