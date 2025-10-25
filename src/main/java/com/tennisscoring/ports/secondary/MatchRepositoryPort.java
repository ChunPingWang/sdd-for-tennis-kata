package com.tennisscoring.ports.secondary;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;

import java.util.List;
import java.util.Optional;

/**
 * Secondary Port for Match Repository Operations
 * 比賽資料持久化操作的次要埠介面
 * 
 * This interface defines the outbound operations for persisting and retrieving
 * tennis match data. It abstracts the data storage mechanism from the domain logic.
 * 
 * Requirements: 1.3, 6.3
 */
public interface MatchRepositoryPort {
    
    /**
     * Saves a match to the repository
     * 將比賽儲存到資料庫
     * 
     * @param match The match to save
     * @return The saved match (may include generated IDs or timestamps)
     */
    Match save(Match match);
    
    /**
     * Finds a match by its unique identifier
     * 根據唯一識別碼查找比賽
     * 
     * @param matchId The unique identifier of the match
     * @return Optional containing the match if found, empty otherwise
     */
    Optional<Match> findById(String matchId);
    
    /**
     * Retrieves all matches from the repository
     * 從資料庫中查詢所有比賽
     * 
     * @return List of all matches
     */
    List<Match> findAll();
    
    /**
     * Deletes a match by its unique identifier
     * 根據唯一識別碼刪除比賽
     * 
     * @param matchId The unique identifier of the match to delete
     */
    void deleteById(String matchId);
    
    /**
     * Checks if a match exists with the given identifier
     * 檢查指定識別碼的比賽是否存在
     * 
     * @param matchId The unique identifier of the match
     * @return true if match exists, false otherwise
     */
    boolean existsById(String matchId);
    
    /**
     * Finds matches by their status
     * 根據狀態查找比賽
     * 
     * @param status The match status to filter by
     * @return List of matches with the specified status
     */
    List<Match> findByStatus(MatchStatus status);
    
    /**
     * Counts the total number of matches in the repository
     * 計算資料庫中比賽的總數
     * 
     * @return The total number of matches
     */
    long count();
    
    /**
     * Counts matches by status
     * 根據狀態計算比賽數量
     * 
     * @param status The match status to count
     * @return The number of matches with the specified status
     */
    long countByStatus(MatchStatus status);
}