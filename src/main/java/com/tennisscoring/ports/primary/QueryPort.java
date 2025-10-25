package com.tennisscoring.ports.primary;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;

import java.util.List;

/**
 * Primary Port for Query Operations
 * 查詢操作的主要埠介面
 * 
 * This interface defines the inbound operations for querying tennis match data,
 * including retrieving individual matches, all matches, and matches by status.
 * 
 * Requirements: 1.1, 2.1, 7.1
 */
public interface QueryPort {
    
    /**
     * Retrieves a specific match by its ID
     * 根據ID查詢特定比賽
     * 
     * @param matchId The unique identifier of the match
     * @return The match with the specified ID
     * @throws MatchNotFoundException if match is not found
     */
    Match getMatch(String matchId);
    
    /**
     * Retrieves all matches in the system
     * 查詢系統中的所有比賽
     * 
     * @return List of all matches
     */
    List<Match> getAllMatches();
    
    /**
     * Retrieves matches filtered by their status
     * 根據狀態篩選查詢比賽
     * 
     * @param status The match status to filter by
     * @return List of matches with the specified status
     */
    List<Match> getMatchesByStatus(MatchStatus status);
    
    /**
     * Checks if a match exists with the given ID
     * 檢查指定ID的比賽是否存在
     * 
     * @param matchId The unique identifier of the match
     * @return true if match exists, false otherwise
     */
    boolean matchExists(String matchId);
}