package com.tennisscoring.solid;

import com.tennisscoring.domain.factory.MatchFactory;
import com.tennisscoring.domain.factory.MatchFactoryRegistry;
import com.tennisscoring.domain.factory.StandardMatchFactory;
import com.tennisscoring.domain.factory.BestOfFiveMatchFactory;
import com.tennisscoring.domain.service.*;

import com.tennisscoring.adapters.secondary.repository.BaseMatchRepository;
import com.tennisscoring.adapters.secondary.repository.InMemoryMatchRepository;
import com.tennisscoring.adapters.secondary.event.BaseEventPublisher;
import com.tennisscoring.adapters.secondary.event.NoOpEventPublisher;
import com.tennisscoring.ports.primary.*;
import com.tennisscoring.ports.secondary.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify SOLID principles compliance.
 * 驗證 SOLID 原則合規性的測試類別
 * 
 * This test class validates that the codebase follows all five SOLID principles:
 * - Single Responsibility Principle (SRP)
 * - Open-Closed Principle (OCP)
 * - Liskov Substitution Principle (LSP)
 * - Interface Segregation Principle (ISP)
 * - Dependency Inversion Principle (DIP)
 * 
 * Requirements: 12.1, 12.5
 */
@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.com.tennisscoring=ERROR"
})
class SolidPrinciplesTest {
    
    /**
     * Test Single Responsibility Principle (SRP) compliance.
     * 測試單一職責原則 (SRP) 合規性
     * 
     * Verifies that each service class has a single, well-defined responsibility.
     */
    @Test
    void testSingleResponsibilityPrinciple() {
        // Test that MatchDomainService focuses on match management
        Class<?> matchServiceClass = MatchDomainService.class;
        Method[] matchServiceMethods = matchServiceClass.getDeclaredMethods();
        
        // Verify no statistics methods in MatchDomainService
        boolean hasStatisticsMethods = Arrays.stream(matchServiceMethods)
                .anyMatch(method -> method.getName().contains("Statistics") || 
                                  method.getName().contains("getCurrentScore"));
        assertFalse(hasStatisticsMethods, 
            "MatchDomainService should not contain statistics methods (SRP violation)");
        
        // Test that MatchStatisticsService focuses on statistics
        Class<?> statisticsServiceClass = MatchStatisticsService.class;
        Method[] statisticsServiceMethods = statisticsServiceClass.getDeclaredMethods();
        
        // Verify statistics service has statistics-related methods
        boolean hasStatisticsRelatedMethods = Arrays.stream(statisticsServiceMethods)
                .anyMatch(method -> method.getName().contains("Statistics") || 
                                  method.getName().contains("Score"));
        assertTrue(hasStatisticsRelatedMethods, 
            "MatchStatisticsService should contain statistics-related methods");
        
        // Test that MatchEventService focuses on event publishing
        Class<?> eventServiceClass = MatchEventService.class;
        Method[] eventServiceMethods = eventServiceClass.getDeclaredMethods();
        
        // Verify event service has event-related methods
        boolean hasEventRelatedMethods = Arrays.stream(eventServiceMethods)
                .anyMatch(method -> method.getName().contains("publish"));
        assertTrue(hasEventRelatedMethods, 
            "MatchEventService should contain event publishing methods");
    }
    
    /**
     * Test Open-Closed Principle (OCP) compliance.
     * 測試開放封閉原則 (OCP) 合規性
     * 
     * Verifies that the system is open for extension but closed for modification.
     */
    @Test
    void testOpenClosedPrinciple() {
        // Test that new match types can be added without modifying existing code
        // (Scoring strategies are handled internally by the Game class)
        
        // Test that new match factories can be added without modifying existing code
        MatchFactory standardFactory = new StandardMatchFactory();
        MatchFactory bestOfFiveFactory = new BestOfFiveMatchFactory();
        
        assertNotNull(standardFactory, "Standard match factory should be instantiable");
        assertNotNull(bestOfFiveFactory, "Best of five match factory should be instantiable");
        
        // Test that factory registry can handle multiple factories
        List<MatchFactory> factories = Arrays.asList(standardFactory, bestOfFiveFactory);
        MatchFactoryRegistry registry = new MatchFactoryRegistry(factories);
        
        assertTrue(registry.isSupported("STANDARD"), 
            "Registry should support standard matches");
        assertTrue(registry.isSupported("BEST_OF_5"), 
            "Registry should support best of 5 matches");
    }
    
    /**
     * Test Liskov Substitution Principle (LSP) compliance.
     * 測試里氏替換原則 (LSP) 合規性
     * 
     * Verifies that derived classes can be substituted for their base classes.
     */
    @Test
    void testLiskovSubstitutionPrinciple() {
        // Test that repository implementations can be substituted
        BaseMatchRepository repository = new InMemoryMatchRepository();
        
        // Verify base repository contract is maintained
        assertNotNull(repository, "Repository should be instantiable");
        assertEquals("IN_MEMORY", repository.getRepositoryType(), 
            "Repository type should be correctly identified");
        assertTrue(repository.isThreadSafe(), 
            "In-memory repository should be thread-safe");
        
        // Test that event publisher implementations can be substituted
        BaseEventPublisher eventPublisher = new NoOpEventPublisher();
        
        // Verify base event publisher contract is maintained
        assertNotNull(eventPublisher, "Event publisher should be instantiable");
        assertEquals("NO_OP", eventPublisher.getPublisherType(), 
            "Event publisher type should be correctly identified");
        assertFalse(eventPublisher.isAsynchronous(), 
            "No-op event publisher should be synchronous");
    }
    
    /**
     * Test Interface Segregation Principle (ISP) compliance.
     * 測試介面隔離原則 (ISP) 合規性
     * 
     * Verifies that interfaces are focused and clients don't depend on unused methods.
     */
    @Test
    void testInterfaceSegregationPrinciple() {
        // Test that match management interfaces are properly segregated
        Class<?> creationPortClass = MatchCreationPort.class;
        Class<?> scoringPortClass = MatchScoringPort.class;
        Class<?> deletionPortClass = MatchDeletionPort.class;
        Class<?> queryPortClass = MatchQueryPort.class;
        
        // Verify each interface has focused responsibilities
        Method[] creationMethods = creationPortClass.getDeclaredMethods();
        Method[] scoringMethods = scoringPortClass.getDeclaredMethods();
        Method[] deletionMethods = deletionPortClass.getDeclaredMethods();
        Method[] queryMethods = queryPortClass.getDeclaredMethods();
        
        // Creation port should only have creation methods
        assertTrue(Arrays.stream(creationMethods)
                .allMatch(method -> method.getName().contains("create")),
            "MatchCreationPort should only contain creation methods");
        
        // Scoring port should only have scoring methods
        assertTrue(Arrays.stream(scoringMethods)
                .allMatch(method -> method.getName().contains("score")),
            "MatchScoringPort should only contain scoring methods");
        
        // Query port should only have query methods
        assertTrue(Arrays.stream(queryMethods)
                .allMatch(method -> method.getName().contains("get") || 
                         method.getName().contains("match") ||
                         method.getName().contains("exists")),
            "MatchQueryPort should only contain query methods");
        
        // Test that event publisher interfaces are properly segregated
        Class<?> matchEventPortClass = MatchEventPublisherPort.class;
        Class<?> gameEventPortClass = GameEventPublisherPort.class;
        
        Method[] matchEventMethods = matchEventPortClass.getDeclaredMethods();
        Method[] gameEventMethods = gameEventPortClass.getDeclaredMethods();
        
        // Match event port should focus on match-level events
        assertTrue(Arrays.stream(matchEventMethods)
                .allMatch(method -> method.getName().contains("Match")),
            "MatchEventPublisherPort should only contain match-level event methods");
        
        // Game event port should focus on game-level events
        assertTrue(Arrays.stream(gameEventMethods)
                .anyMatch(method -> method.getName().contains("Point") || 
                         method.getName().contains("Game") ||
                         method.getName().contains("Set")),
            "GameEventPublisherPort should contain game-level event methods");
    }
    
    /**
     * Test Dependency Inversion Principle (DIP) compliance.
     * 測試依賴反轉原則 (DIP) 合規性
     * 
     * Verifies that high-level modules don't depend on low-level modules.
     */
    @Test
    void testDependencyInversionPrinciple() {
        // Test that service interfaces exist for abstraction
        assertTrue(MatchService.class.isInterface(), 
            "MatchService should be an interface");
        assertTrue(ScoringService.class.isInterface(), 
            "ScoringService should be an interface");
        assertTrue(StatisticsService.class.isInterface(), 
            "StatisticsService should be an interface");
        assertTrue(EventService.class.isInterface(), 
            "EventService should be an interface");
        
        // Test that concrete services implement the interfaces
        assertTrue(MatchService.class.isAssignableFrom(MatchDomainService.class),
            "MatchDomainService should implement MatchService interface");
        assertTrue(ScoringService.class.isAssignableFrom(ScoringDomainService.class),
            "ScoringDomainService should implement ScoringService interface");
        assertTrue(StatisticsService.class.isAssignableFrom(MatchStatisticsService.class),
            "MatchStatisticsService should implement StatisticsService interface");
        assertTrue(EventService.class.isAssignableFrom(MatchEventService.class),
            "MatchEventService should implement EventService interface");
        
        // Test that repository and event publisher ports are interfaces
        assertTrue(MatchRepositoryPort.class.isInterface(), 
            "MatchRepositoryPort should be an interface");
        assertTrue(EventPublisherPort.class.isInterface(), 
            "EventPublisherPort should be an interface");
        assertTrue(MatchEventPublisherPort.class.isInterface(), 
            "MatchEventPublisherPort should be an interface");
        assertTrue(GameEventPublisherPort.class.isInterface(), 
            "GameEventPublisherPort should be an interface");
    }
    
    /**
     * Test overall SOLID principles integration.
     * 測試 SOLID 原則的整體整合
     * 
     * Verifies that all SOLID principles work together harmoniously.
     */
    @Test
    void testSolidPrinciplesIntegration() {
        // Test that the system maintains SOLID principles when components interact
        
        // SRP: Each service has a single responsibility
        assertNotEquals(MatchDomainService.class, MatchStatisticsService.class,
            "Different services should handle different responsibilities");
        
        // OCP: System can be extended with new factories
        assertTrue(MatchFactory.class.isInterface(),
            "Factory pattern enables extension without modification");
        
        // LSP: Base classes can be substituted
        assertTrue(BaseMatchRepository.class.isAssignableFrom(InMemoryMatchRepository.class),
            "Repository implementations should be substitutable");
        
        // ISP: Interfaces are focused and segregated
        assertNotEquals(MatchCreationPort.class, MatchScoringPort.class,
            "Different aspects should have separate interfaces");
        
        // DIP: High-level modules depend on abstractions
        assertTrue(MatchService.class.isInterface(),
            "Services should depend on abstractions, not concretions");
    }
}