package com.tennisscoring.domain.factory;

import com.tennisscoring.domain.model.Match;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Registry for managing different match factories.
 * 管理不同比賽工廠的註冊表
 * 
 * This registry follows the Open-Closed Principle by allowing
 * new match factories to be registered without modifying existing code.
 * 
 * Requirements: 2.1, 2.3, 5.1, 5.2
 */
@Component
public class MatchFactoryRegistry {
    
    private final List<MatchFactory> factories;
    private final MatchFactory defaultFactory;
    
    public MatchFactoryRegistry(List<MatchFactory> factories) {
        this.factories = Objects.requireNonNull(factories, "Factories list cannot be null");
        
        // Find the default factory (StandardMatchFactory)
        this.defaultFactory = factories.stream()
                .filter(factory -> factory instanceof StandardMatchFactory)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No default match factory found"));
    }
    
    /**
     * Create a match using the appropriate factory for the given type.
     * 使用適當的工廠為給定類型創建比賽
     * 
     * @param matchType the type of match to create (null for default)
     * @param player1Name name of the first player
     * @param player2Name name of the second player
     * @return the created match
     */
    public Match createMatch(String matchType, String player1Name, String player2Name) {
        MatchFactory factory = findFactory(matchType);
        return factory.createMatch(player1Name, player2Name);
    }
    
    /**
     * Create a match using the default factory.
     * 使用預設工廠創建比賽
     * 
     * @param player1Name name of the first player
     * @param player2Name name of the second player
     * @return the created match
     */
    public Match createMatch(String player1Name, String player2Name) {
        return defaultFactory.createMatch(player1Name, player2Name);
    }
    
    /**
     * Find the appropriate factory for the given match type.
     * 為給定的比賽類型找到適當的工廠
     * 
     * @param matchType the match type
     * @return the matching factory
     */
    private MatchFactory findFactory(String matchType) {
        return factories.stream()
                .filter(factory -> factory.supports(matchType))
                .findFirst()
                .orElse(defaultFactory);
    }
    
    /**
     * Get all available match types.
     * 獲取所有可用的比賽類型
     * 
     * @return list of supported match types
     */
    public List<String> getSupportedMatchTypes() {
        return factories.stream()
                .map(MatchFactory::getMatchType)
                .toList();
    }
    
    /**
     * Check if a match type is supported.
     * 檢查是否支援某種比賽類型
     * 
     * @param matchType the match type to check
     * @return true if supported
     */
    public boolean isSupported(String matchType) {
        return factories.stream()
                .anyMatch(factory -> factory.supports(matchType));
    }
}