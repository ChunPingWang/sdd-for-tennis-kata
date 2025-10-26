package com.tennisscoring.adapters.secondary.repository;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;
import com.tennisscoring.ports.secondary.MatchRepositoryPort;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for match repository implementations.
 * 比賽儲存庫實作的抽象基底類別
 * 
 * This class follows the Liskov Substitution Principle by providing
 * a common contract and shared behavior that all repository implementations
 * must follow. Subclasses can be substituted without breaking functionality.
 * 
 * Requirements: 1.3, 6.3
 */
public abstract class BaseMatchRepository implements MatchRepositoryPort {
    
    /**
     * Template method for saving a match with validation.
     * 儲存比賽的模板方法，包含驗證
     * 
     * @param match the match to save
     * @return the saved match
     */
    @Override
    public final Match save(Match match) {
        Objects.requireNonNull(match, "Match cannot be null");
        validateMatch(match);
        
        return doSave(match);
    }
    
    /**
     * Template method for finding a match by ID with validation.
     * 根據ID查找比賽的模板方法，包含驗證
     * 
     * @param matchId the match ID
     * @return optional containing the match if found
     */
    @Override
    public final Optional<Match> findById(String matchId) {
        validateMatchId(matchId);
        
        return doFindById(matchId);
    }
    
    /**
     * Template method for deleting a match by ID with validation.
     * 根據ID刪除比賽的模板方法，包含驗證
     * 
     * @param matchId the match ID
     */
    @Override
    public final void deleteById(String matchId) {
        validateMatchId(matchId);
        
        doDeleteById(matchId);
    }
    
    /**
     * Template method for checking if a match exists with validation.
     * 檢查比賽是否存在的模板方法，包含驗證
     * 
     * @param matchId the match ID
     * @return true if match exists
     */
    @Override
    public final boolean existsById(String matchId) {
        validateMatchId(matchId);
        
        return doExistsById(matchId);
    }
    
    /**
     * Template method for finding matches by status with validation.
     * 根據狀態查找比賽的模板方法，包含驗證
     * 
     * @param status the match status
     * @return list of matches with the specified status
     */
    @Override
    public final List<Match> findByStatus(MatchStatus status) {
        Objects.requireNonNull(status, "Match status cannot be null");
        
        return doFindByStatus(status);
    }
    
    // Abstract methods that subclasses must implement
    
    /**
     * Perform the actual save operation.
     * 執行實際的儲存操作
     * 
     * @param match the match to save
     * @return the saved match
     */
    protected abstract Match doSave(Match match);
    
    /**
     * Perform the actual find by ID operation.
     * 執行實際的根據ID查找操作
     * 
     * @param matchId the match ID
     * @return optional containing the match if found
     */
    protected abstract Optional<Match> doFindById(String matchId);
    
    /**
     * Perform the actual delete by ID operation.
     * 執行實際的根據ID刪除操作
     * 
     * @param matchId the match ID
     */
    protected abstract void doDeleteById(String matchId);
    
    /**
     * Perform the actual exists by ID check.
     * 執行實際的根據ID存在性檢查
     * 
     * @param matchId the match ID
     * @return true if match exists
     */
    protected abstract boolean doExistsById(String matchId);
    
    /**
     * Perform the actual find by status operation.
     * 執行實際的根據狀態查找操作
     * 
     * @param status the match status
     * @return list of matches with the specified status
     */
    protected abstract List<Match> doFindByStatus(MatchStatus status);
    
    // Common validation methods
    
    /**
     * Validate a match object.
     * 驗證比賽物件
     * 
     * @param match the match to validate
     * @throws IllegalArgumentException if match is invalid
     */
    protected void validateMatch(Match match) {
        if (match.getMatchId() == null || match.getMatchId().trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
        
        if (match.getPlayer1() == null || match.getPlayer2() == null) {
            throw new IllegalArgumentException("Match must have two players");
        }
        
        if (match.getStatus() == null) {
            throw new IllegalArgumentException("Match status cannot be null");
        }
    }
    
    /**
     * Validate a match ID.
     * 驗證比賽ID
     * 
     * @param matchId the match ID to validate
     * @throws IllegalArgumentException if match ID is invalid
     */
    protected void validateMatchId(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
    }
    
    /**
     * Get repository type for logging/debugging purposes.
     * 獲取儲存庫類型用於日誌/除錯目的
     * 
     * @return the repository type name
     */
    public abstract String getRepositoryType();
    
    /**
     * Check if this repository supports concurrent access.
     * 檢查此儲存庫是否支援並發存取
     * 
     * @return true if thread-safe
     */
    public abstract boolean isThreadSafe();
}