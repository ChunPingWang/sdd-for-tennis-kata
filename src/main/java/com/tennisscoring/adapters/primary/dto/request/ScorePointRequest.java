package com.tennisscoring.adapters.primary.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request model for scoring a point in a tennis match.
 * 網球比賽得分的請求模型
 */
public class ScorePointRequest {
    
    @NotBlank(message = "球員ID不能為空")
    private String playerId;
    
    /**
     * Default constructor for JSON deserialization.
     */
    public ScorePointRequest() {
    }
    
    /**
     * Constructor with player ID.
     * 
     * @param playerId the ID of the player who scored
     */
    public ScorePointRequest(String playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    @Override
    public String toString() {
        return "ScorePointRequest{" +
                "playerId='" + playerId + '\'' +
                '}';
    }
}