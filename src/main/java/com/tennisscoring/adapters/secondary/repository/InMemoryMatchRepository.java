package com.tennisscoring.adapters.secondary.repository;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of MatchRepositoryPort using ConcurrentHashMap for thread safety.
 * 使用 ConcurrentHashMap 實作執行緒安全的記憶體儲存
 * 
 * This implementation follows the Liskov Substitution Principle by extending
 * BaseMatchRepository and can be substituted with any other repository implementation
 * without breaking functionality.
 * 
 * Requirements: 1.3, 9.4
 */
@Repository
public class InMemoryMatchRepository extends BaseMatchRepository {
    
    private final ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<>();
    
    @Override
    protected Match doSave(Match match) {
        matches.put(match.getMatchId(), match);
        return match;
    }
    
    @Override
    protected Optional<Match> doFindById(String matchId) {
        return Optional.ofNullable(matches.get(matchId));
    }
    
    @Override
    public List<Match> findAll() {
        return new ArrayList<>(matches.values());
    }
    
    @Override
    protected void doDeleteById(String matchId) {
        matches.remove(matchId);
    }
    
    @Override
    protected boolean doExistsById(String matchId) {
        return matches.containsKey(matchId);
    }
    
    @Override
    protected List<Match> doFindByStatus(MatchStatus status) {
        return matches.values().stream()
                .filter(match -> status.equals(match.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return matches.size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long countByStatus(MatchStatus status) {
        if (status == null) {
            return 0;
        }
        
        return matches.values().stream()
                .filter(match -> status.equals(match.getStatus()))
                .count();
    }
    
    /**
     * Clear all matches from the repository.
     * Useful for testing purposes.
     * 清除所有比賽記錄，主要用於測試
     */
    public void clear() {
        matches.clear();
    }
    
    /**
     * Get the current size of the repository.
     * 取得目前儲存庫的大小
     * 
     * @return the number of matches stored
     */
    public int size() {
        return matches.size();
    }
    
    /**
     * Check if the repository is empty.
     * 檢查儲存庫是否為空
     * 
     * @return true if no matches are stored
     */
    public boolean isEmpty() {
        return matches.isEmpty();
    }
    
    @Override
    public String getRepositoryType() {
        return "IN_MEMORY";
    }
    
    @Override
    public boolean isThreadSafe() {
        return true;
    }
}