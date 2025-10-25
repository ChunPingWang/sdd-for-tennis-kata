package com.tennisscoring.adapters.primary.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response model for tennis match information.
 * 網球比賽資訊的回應模型
 */
public class MatchResponse {
    
    private String matchId;
    private PlayerResponse player1;
    private PlayerResponse player2;
    private String status;
    private String currentScore;
    private int currentSetNumber;
    private int currentGameNumber;
    private boolean isCurrentGameTiebreak;
    private PlayerResponse winner;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<SetResponse> sets;
    
    /**
     * Default constructor for JSON serialization.
     */
    public MatchResponse() {
    }
    
    public String getMatchId() {
        return matchId;
    }
    
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
    
    public PlayerResponse getPlayer1() {
        return player1;
    }
    
    public void setPlayer1(PlayerResponse player1) {
        this.player1 = player1;
    }
    
    public PlayerResponse getPlayer2() {
        return player2;
    }
    
    public void setPlayer2(PlayerResponse player2) {
        this.player2 = player2;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCurrentScore() {
        return currentScore;
    }
    
    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }
    
    public int getCurrentSetNumber() {
        return currentSetNumber;
    }
    
    public void setCurrentSetNumber(int currentSetNumber) {
        this.currentSetNumber = currentSetNumber;
    }
    
    public int getCurrentGameNumber() {
        return currentGameNumber;
    }
    
    public void setCurrentGameNumber(int currentGameNumber) {
        this.currentGameNumber = currentGameNumber;
    }
    
    public boolean isCurrentGameTiebreak() {
        return isCurrentGameTiebreak;
    }
    
    public void setCurrentGameTiebreak(boolean currentGameTiebreak) {
        isCurrentGameTiebreak = currentGameTiebreak;
    }
    
    public PlayerResponse getWinner() {
        return winner;
    }
    
    public void setWinner(PlayerResponse winner) {
        this.winner = winner;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public List<SetResponse> getSets() {
        return sets;
    }
    
    public void setSets(List<SetResponse> sets) {
        this.sets = sets;
    }
    
    @Override
    public String toString() {
        return "MatchResponse{" +
                "matchId='" + matchId + '\'' +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", status='" + status + '\'' +
                ", currentScore='" + currentScore + '\'' +
                ", currentSetNumber=" + currentSetNumber +
                ", currentGameNumber=" + currentGameNumber +
                ", isCurrentGameTiebreak=" + isCurrentGameTiebreak +
                ", winner=" + winner +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                ", sets=" + sets +
                '}';
    }
}