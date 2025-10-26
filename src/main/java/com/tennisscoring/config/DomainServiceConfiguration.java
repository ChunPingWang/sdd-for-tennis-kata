package com.tennisscoring.config;

import com.tennisscoring.domain.service.*;
import com.tennisscoring.domain.factory.MatchFactoryRegistry;

import com.tennisscoring.ports.secondary.MatchRepositoryPort;
import com.tennisscoring.ports.secondary.MatchEventPublisherPort;
import com.tennisscoring.ports.secondary.GameEventPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for domain service dependency injection.
 * 領域服務依賴注入的配置類別
 * 
 * This configuration follows the Dependency Inversion Principle by
 * managing the creation and wiring of service abstractions.
 * 
 * Requirements: 10.4, 10.5
 */
@Configuration
public class DomainServiceConfiguration {
    
    /**
     * Configure the primary match service implementation.
     * 配置主要的比賽服務實作
     * 
     * @param matchRepository the match repository
     * @param scoringService the scoring service
     * @param eventService the event service
     * @param matchFactory the match factory registry
     * @param validationService the validation service
     * @return the match service implementation
     */
    @Bean
    @Primary
    public MatchService matchService(
            MatchRepositoryPort matchRepository,
            ScoringDomainService scoringService,
            MatchEventService eventService,
            MatchFactoryRegistry matchFactory,
            ValidationService validationService) {
        
        return new MatchDomainService(
            matchRepository,
            scoringService,
            eventService,
            matchFactory,
            validationService
        );
    }
    
    /**
     * Configure the primary scoring service implementation.
     * 配置主要的計分服務實作
     * 
     * @param validationService the validation service
     * @param strategyRegistry the scoring strategy registry
     * @return the scoring service implementation
     */
    @Bean
    @Primary
    public ScoringDomainService scoringService(ValidationService validationService) {
        return new ScoringDomainService(validationService);
    }
    
    /**
     * Configure the primary statistics service implementation.
     * 配置主要的統計服務實作
     * 
     * @param matchRepository the match repository
     * @param scoringService the scoring service
     * @param validationService the validation service
     * @return the statistics service implementation
     */
    @Bean
    @Primary
    public StatisticsService statisticsService(
            MatchRepositoryPort matchRepository,
            ScoringDomainService scoringService,
            ValidationService validationService) {
        
        return new MatchStatisticsService(
            matchRepository,
            scoringService,
            validationService
        );
    }
    
    /**
     * Configure the primary event service implementation.
     * 配置主要的事件服務實作
     * 
     * @param matchEventPublisher the match event publisher
     * @param gameEventPublisher the game event publisher
     * @return the event service implementation
     */
    @Bean
    @Primary
    public MatchEventService eventService(
            MatchEventPublisherPort matchEventPublisher,
            GameEventPublisherPort gameEventPublisher) {
        
        return new MatchEventService(matchEventPublisher, gameEventPublisher);
    }
}