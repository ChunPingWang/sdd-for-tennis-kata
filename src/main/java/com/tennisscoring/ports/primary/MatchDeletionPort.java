package com.tennisscoring.ports.primary;

import com.tennisscoring.domain.model.Match;

/**
 * Port for match deletion operations.
 * 比賽刪除操作的埠介面
 * 
 * This interface follows the Interface Segregation Principle by focusing
 * solely on match deletion functionality.
 * 
 * Requirements: 1.1, 7.1
 */
public interface MatchDeletionPort {
    
    /**
     * Deletes a match from the system.
     * 從系統中刪除比賽
     * 
     * @param matchId The unique identifier of the match to delete
     * @throws MatchNotFoundException if match is not found
     */
    void deleteMatch(String matchId);
    
    /**
     * Cancels a match that is in progress.
     * 取消進行中的比賽
     * 
     * @param matchId The unique identifier of the match to cancel
     * @return The cancelled match
     * @throws MatchNotFoundException if match is not found
     * @throws InvalidMatchStateException if match cannot be cancelled
     */
    Match cancelMatch(String matchId);
}