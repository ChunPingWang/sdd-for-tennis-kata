package com.tennisscoring.config;

import com.tennisscoring.adapters.secondary.event.NoOpEventPublisher;
import com.tennisscoring.adapters.secondary.repository.InMemoryMatchRepository;
import com.tennisscoring.ports.secondary.EventPublisherPort;
import com.tennisscoring.ports.secondary.MatchRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration for dependency injection.
 * 依賴注入的應用程式配置
 */
@Configuration
public class ApplicationConfig {
    
    /**
     * Configure the match repository implementation.
     * 配置比賽資料庫實作
     * 
     * @return the match repository implementation
     */
    @Bean
    public MatchRepositoryPort matchRepository() {
        return new InMemoryMatchRepository();
    }
    
    /**
     * Configure the event publisher implementation.
     * 配置事件發布器實作
     * 
     * @return the event publisher implementation
     */
    @Bean
    public EventPublisherPort eventPublisher() {
        return new NoOpEventPublisher();
    }
}