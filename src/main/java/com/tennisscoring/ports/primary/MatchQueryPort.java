package com.tennisscoring.ports.primary;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;

import java.util.List;

/**
 * Port for match query operations.
 * 比賽查詢操作的埠介面
 * 
 * This interface follows the Interface Segregation Principle by focusing
 * solely on query functionality.
 * 
 * Requirements: 7.1
 */
public interface MatchQueryPort {
    
    /**
     * Retrieves a match by its unique identifier.
     * 根據唯一識別碼檢索比賽
     * 
     * @param matchId The unique identifier of the match
     * @return The match with the specified ID
     * @throws MatchNotFoundException if match is not found
     */
    Match getMatch(String matchId);
    
    /**
     * Retrieves all matches in the system.
     * 檢索系統中的所有比賽
     * 
     * @return List of all matches
     */
    List<Match> getAllMatches();
    
    /**
     * Retrieves matches by their status.
     * 根據狀態檢索比賽
     * 
     * @param status The match status to filter by
     * @return List of matches with the specified status
     */
    List<Match> getMatchesByStatus(MatchStatus status);
    
    /**
     * Checks if a match exists with the given ID.
     * 檢查是否存在具有給定ID的比賽
     * 
     * @param matchId The match ID to check
     * @return true if match exists, false otherwise
     */
    boolean matchExists(String matchId);
}