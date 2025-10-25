package com.tennisscoring.adapters.secondary.repository;

import com.tennisscoring.domain.model.Match;
import com.tennisscoring.domain.model.MatchStatus;
import com.tennisscoring.ports.secondary.MatchRepositoryPort;
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
 * This implementation provides thread-safe operations for storing and retrieving
 * tennis match data in memory. Suitable for development and testing environments.
 * 
 * Requirements: 1.3, 9.4
 */
@Repository
public class InMemoryMatchRepository implements MatchRepositoryPort {
    
    private final ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Match save(Match match) {
        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null");
        }
        
        String matchId = match.getMatchId();
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
        
        matches.put(matchId, match);
        return match;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Match> findById(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(matches.get(matchId));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Match> findAll() {
        return new ArrayList<>(matches.values());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(String matchId) {
        if (matchId != null && !matchId.trim().isEmpty()) {
            matches.remove(matchId);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            return false;
        }
        
        return matches.containsKey(matchId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Match> findByStatus(MatchStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }
        
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
}