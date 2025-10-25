package com.tennisscoring.adapters.primary.dto.response;

import java.util.List;

/**
 * Response model for set information.
 * 盤資訊的回應模型
 */
public class SetResponse {
    
    private int setNumber;
    private int player1Games;
    private int player2Games;
    private boolean isCompleted;
    private String winnerId;
    private boolean hasTiebreak;
    private TiebreakResponse tiebreak;
    private List<GameResponse> games;
    
    /**
     * Default constructor for JSON serialization.
     */
    public SetResponse() {
    }
    
    /**
     * Constructor with basic set information.
     * 
     * @param setNumber the set number
     * @param player1Games games won by player 1
     * @param player2Games games won by player 2
     * @param isCompleted whether the set is completed
     */
    public SetResponse(int setNumber, int player1Games, int player2Games, boolean isCompleted) {
        this.setNumber = setNumber;
        this.player1Games = player1Games;
        this.player2Games = player2Games;
        this.isCompleted = isCompleted;
    }
    
    public int getSetNumber() {
        return setNumber;
    }
    
    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }
    
    public int getPlayer1Games() {
        return player1Games;
    }
    
    public void setPlayer1Games(int player1Games) {
        this.player1Games = player1Games;
    }
    
    public int getPlayer2Games() {
        return player2Games;
    }
    
    public void setPlayer2Games(int player2Games) {
        this.player2Games = player2Games;
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
    
    public boolean isHasTiebreak() {
        return hasTiebreak;
    }
    
    public void setHasTiebreak(boolean hasTiebreak) {
        this.hasTiebreak = hasTiebreak;
    }
    
    public TiebreakResponse getTiebreak() {
        return tiebreak;
    }
    
    public void setTiebreak(TiebreakResponse tiebreak) {
        this.tiebreak = tiebreak;
    }
    
    public List<GameResponse> getGames() {
        return games;
    }
    
    public void setGames(List<GameResponse> games) {
        this.games = games;
    }
    
    @Override
    public String toString() {
        return "SetResponse{" +
                "setNumber=" + setNumber +
                ", player1Games=" + player1Games +
                ", player2Games=" + player2Games +
                ", isCompleted=" + isCompleted +
                ", winnerId='" + winnerId + '\'' +
                ", hasTiebreak=" + hasTiebreak +
                ", tiebreak=" + tiebreak +
                ", games=" + games +
                '}';
    }
}