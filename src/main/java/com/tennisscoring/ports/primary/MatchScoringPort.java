package com.tennisscoring.ports.primary;

import com.tennisscoring.domain.model.Match;

/**
 * Port for match scoring operations.
 * 比賽計分操作的埠介面
 * 
 * This interface follows the Interface Segregation Principle by focusing
 * solely on scoring functionality.
 * 
 * Requirements: 2.1, 7.1
 */
public interface MatchScoringPort {
    
    /**
     * Records a point scored by a player in a match.
     * 記錄球員在比賽中的得分
     * 
     * @param matchId The unique identifier of the match
     * @param playerId The unique identifier of the player who scored
     * @return The updated match with new score
     * @throws MatchNotFoundException if match is not found
     * @throws InvalidMatchStateException if match is already completed
     * @throws IllegalArgumentException if player is not in the match
     */
    Match scorePoint(String matchId, String playerId);
}