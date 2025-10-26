package com.tennisscoring.domain.service;

import com.tennisscoring.domain.exception.MatchNotFoundException;
import com.tennisscoring.domain.exception.ValidationException;
import com.tennisscoring.domain.model.*;
import com.tennisscoring.domain.factory.MatchFactoryRegistry;
import com.tennisscoring.ports.secondary.MatchRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchDomainService SOLID Refactored Tests")
class MatchDomainServiceRefactoredTest {

    @Mock
    private MatchRepositoryPort matchRepository;

    @Mock
    private ScoringDomainService scoringService;

    @Mock
    private MatchEventService eventService;

    @Mock
    private MatchFactoryRegistry matchFactory;

    @Mock
    private ValidationService validationService;

    private MatchDomainService matchDomainService;

    @BeforeEach
    void setUp() {
        matchDomainService = new MatchDomainService(
            matchRepository, 
            scoringService, 
            eventService,
            matchFactory,
            validationService
        );
    }

    @Test
    @DisplayName("Should create match successfully")
    void shouldCreateMatchSuccessfully() {
        // Given
        String player1Name = "John Doe";
        String player2Name = "Jane Smith";
        Match expectedMatch = Match.create(player1Name, player2Name);
        
        when(matchFactory.createMatch(null, player1Name, player2Name)).thenReturn(expectedMatch);
        when(matchRepository.save(any(Match.class))).thenReturn(expectedMatch);
        doNothing().when(validationService).validatePlayerNames(player1Name, player2Name);
        doNothing().when(eventService).publishMatchCreated(any(Match.class));

        // When
        Match result = matchDomainService.createMatch(player1Name, player2Name);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPlayer1().getName()).isEqualTo(player1Name);
        assertThat(result.getPlayer2().getName()).isEqualTo(player2Name);
        
        verify(validationService).validatePlayerNames(player1Name, player2Name);
        verify(matchFactory).createMatch(null, player1Name, player2Name);
        verify(matchRepository).save(any(Match.class));
        verify(eventService).publishMatchCreated(any(Match.class));
    }

    @Test
    @DisplayName("Should score point successfully")
    void shouldScorePointSuccessfully() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        String playerId = match.getPlayer1().getPlayerId().getValue();
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);
        when(scoringService.scorePoint(eq(match), any(PlayerId.class))).thenReturn(false);
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(validationService).validatePlayerId(playerId);
        doNothing().when(eventService).publishPointScored(any(Match.class), eq(playerId));
        doNothing().when(eventService).publishGameStateEvents(eq(match), any(PlayerId.class));

        // When
        Match result = matchDomainService.scorePoint(matchId, playerId);

        // Then
        assertThat(result).isNotNull();
        
        verify(validationService).validateMatchId(matchId);
        verify(validationService).validatePlayerId(playerId);
        verify(matchRepository).findById(matchId);
        verify(scoringService).scorePoint(eq(match), any(PlayerId.class));
        verify(matchRepository).save(any(Match.class));
        verify(eventService).publishPointScored(any(Match.class), eq(playerId));
        verify(eventService).publishGameStateEvents(eq(match), any(PlayerId.class));
    }

    @Test
    @DisplayName("Should get match successfully")
    void shouldGetMatchSuccessfully() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match expectedMatch = Match.create("John Doe", "Jane Smith");
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(expectedMatch));
        doNothing().when(validationService).validateMatchId(matchId);

        // When
        Match result = matchDomainService.getMatch(matchId);

        // Then
        assertThat(result).isEqualTo(expectedMatch);
        
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).findById(matchId);
    }

    @Test
    @DisplayName("Should get all matches successfully")
    void shouldGetAllMatchesSuccessfully() {
        // Given
        List<Match> expectedMatches = Arrays.asList(
            Match.create("John Doe", "Jane Smith"),
            Match.create("Alice", "Bob")
        );
        
        when(matchRepository.findAll()).thenReturn(expectedMatches);

        // When
        List<Match> result = matchDomainService.getAllMatches();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedMatches);
        
        verify(matchRepository).findAll();
    }

    @Test
    @DisplayName("Should delete match successfully")
    void shouldDeleteMatchSuccessfully() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        
        when(matchRepository.existsById(matchId)).thenReturn(true);
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(matchRepository).deleteById(matchId);
        doNothing().when(eventService).publishMatchDeleted(matchId, "system");

        // When
        matchDomainService.deleteMatch(matchId);

        // Then
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).existsById(matchId);
        verify(matchRepository).deleteById(matchId);
        verify(eventService).publishMatchDeleted(matchId, "system");
    }

    @Test
    @DisplayName("Should throw exception when match not found")
    void shouldThrowExceptionWhenMatchNotFound() {
        // Given
        String matchId = "non-existent-match";
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        doNothing().when(validationService).validateMatchId(matchId);

        // When & Then
        assertThatThrownBy(() -> matchDomainService.getMatch(matchId))
            .isInstanceOf(MatchNotFoundException.class);
        
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).findById(matchId);
    }

    @Test
    @DisplayName("Should check if match exists")
    void shouldCheckIfMatchExists() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        
        when(matchRepository.existsById(matchId)).thenReturn(true);

        // When
        boolean result = matchDomainService.matchExists(matchId);

        // Then
        assertThat(result).isTrue();
        verify(matchRepository).existsById(matchId);
    }
}