package com.tennisscoring.adapters.primary.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request model for creating a new tennis match.
 * 創建新網球比賽的請求模型
 */
public class CreateMatchRequest {
    
    @NotBlank(message = "第一位球員名稱不能為空")
    @Size(max = 50, message = "球員名稱長度不能超過50個字元")
    private String player1Name;
    
    @NotBlank(message = "第二位球員名稱不能為空")
    @Size(max = 50, message = "球員名稱長度不能超過50個字元")
    private String player2Name;
    
    /**
     * Default constructor for JSON deserialization.
     */
    public CreateMatchRequest() {
    }
    
    /**
     * Constructor with player names.
     * 
     * @param player1Name first player name
     * @param player2Name second player name
     */
    public CreateMatchRequest(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }
    
    public String getPlayer1Name() {
        return player1Name;
    }
    
    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }
    
    public String getPlayer2Name() {
        return player2Name;
    }
    
    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
    
    @Override
    public String toString() {
        return "CreateMatchRequest{" +
                "player1Name='" + player1Name + '\'' +
                ", player2Name='" + player2Name + '\'' +
                '}';
    }
}