package com.tennisscoring.domain.service;

import com.tennisscoring.domain.exception.MatchNotFoundException;
import com.tennisscoring.domain.exception.ValidationException;
import com.tennisscoring.domain.model.*;
import com.tennisscoring.ports.secondary.EventPublisherPort;
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
@DisplayName("MatchDomainService Unit Tests")
class MatchDomainServiceTest {

    @Mock
    private MatchRepositoryPort matchRepository;

    @Mock
    private ScoringDomainService scoringService;

    @Mock
    private EventPublisherPort eventPublisher;

    @Mock
    private ValidationService validationService;

    private MatchDomainService matchDomainService;

    @BeforeEach
    void setUp() {
        matchDomainService = new MatchDomainService(
            matchRepository, 
            scoringService, 
            eventPublisher, 
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
        
        when(matchRepository.save(any(Match.class))).thenReturn(expectedMatch);
        doNothing().when(validationService).validatePlayerNames(player1Name, player2Name);
        doNothing().when(eventPublisher).publishMatchCreated(any());

        // When
        Match result = matchDomainService.createMatch(player1Name, player2Name);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPlayer1().getName()).isEqualTo(player1Name);
        assertThat(result.getPlayer2().getName()).isEqualTo(player2Name);
        
        verify(validationService).validatePlayerNames(player1Name, player2Name);
        verify(matchRepository).save(any(Match.class));
        verify(eventPublisher).publishMatchCreated(any());
    }

    @Test
    @DisplayName("Should throw exception when creating match with invalid player names")
    void shouldThrowExceptionWhenCreatingMatchWithInvalidPlayerNames() {
        // Given
        String player1Name = "";
        String player2Name = "Jane Smith";
        
        doThrow(new ValidationException("player1Name", player1Name, "Player name cannot be empty"))
            .when(validationService).validatePlayerNames(player1Name, player2Name);

        // When & Then
        assertThatThrownBy(() -> matchDomainService.createMatch(player1Name, player2Name))
            .isInstanceOf(ValidationException.class);
        
        verify(validationService).validatePlayerNames(player1Name, player2Name);
        verifyNoInteractions(matchRepository, eventPublisher);
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
        when(scoringService.scorePoint(any(Match.class), any(PlayerId.class))).thenReturn(false);
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(validationService).validatePlayerId(playerId);
        doNothing().when(eventPublisher).publishPointScored(any());

        // When
        Match result = matchDomainService.scorePoint(matchId, playerId);

        // Then
        assertThat(result).isNotNull();
        
        verify(validationService).validateMatchId(matchId);
        verify(validationService).validatePlayerId(playerId);
        verify(matchRepository).findById(matchId);
        verify(scoringService).scorePoint(any(Match.class), any(PlayerId.class));
        verify(matchRepository).save(match);
        verify(eventPublisher).publishPointScored(any());
    }

    @Test
    @DisplayName("Should publish match completed event when match is won")
    void shouldPublishMatchCompletedEventWhenMatchIsWon() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        String playerId = match.getPlayer1().getPlayerId().getValue();
        
        // Create a mock match that appears completed
        Match completedMatch = mock(Match.class);
        when(completedMatch.getMatchId()).thenReturn(matchId);
        when(completedMatch.getWinner()).thenReturn(PlayerId.of(playerId));
        when(completedMatch.getCurrentScore()).thenReturn("6-4, 6-4");
        when(completedMatch.getSets()).thenReturn(List.of());
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(completedMatch);
        when(scoringService.scorePoint(any(Match.class), any(PlayerId.class))).thenReturn(true);
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(validationService).validatePlayerId(playerId);
        doNothing().when(eventPublisher).publishPointScored(any());
        doNothing().when(eventPublisher).publishMatchCompleted(any());

        // When
        matchDomainService.scorePoint(matchId, playerId);

        // Then
        verify(eventPublisher).publishPointScored(any());
        verify(eventPublisher).publishMatchCompleted(any());
    }

    @Test
    @DisplayName("Should throw exception when scoring point on non-existent match")
    void shouldThrowExceptionWhenScoringPointOnNonExistentMatch() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        String playerId = "123e4567-e89b-12d3-a456-426614174001";
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(validationService).validatePlayerId(playerId);

        // When & Then
        assertThatThrownBy(() -> matchDomainService.scorePoint(matchId, playerId))
            .isInstanceOf(MatchNotFoundException.class);
        
        verify(matchRepository).findById(matchId);
        verifyNoInteractions(scoringService, eventPublisher);
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
    @DisplayName("Should throw exception when getting non-existent match")
    void shouldThrowExceptionWhenGettingNonExistentMatch() {
        // Given
        String matchId = "non-existent-match";
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());
        doNothing().when(validationService).validateMatchId(matchId);

        // When & Then
        assertThatThrownBy(() -> matchDomainService.getMatch(matchId))
            .isInstanceOf(MatchNotFoundException.class);
        
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
        assertThat(result).isEqualTo(expectedMatches);
        verify(matchRepository).findAll();
    }

    @Test
    @DisplayName("Should get matches by status successfully")
    void shouldGetMatchesByStatusSuccessfully() {
        // Given
        MatchStatus status = MatchStatus.IN_PROGRESS;
        List<Match> expectedMatches = Arrays.asList(
            Match.create("John Doe", "Jane Smith")
        );
        
        when(matchRepository.findByStatus(status)).thenReturn(expectedMatches);
        doNothing().when(validationService).validateMatchStatus(status);

        // When
        List<Match> result = matchDomainService.getMatchesByStatus(status);

        // Then
        assertThat(result).isEqualTo(expectedMatches);
        
        verify(validationService).validateMatchStatus(status);
        verify(matchRepository).findByStatus(status);
    }

    @Test
    @DisplayName("Should delete match successfully")
    void shouldDeleteMatchSuccessfully() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        
        when(matchRepository.existsById(matchId)).thenReturn(true);
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(matchRepository).deleteById(matchId);
        doNothing().when(eventPublisher).publishMatchDeleted(matchId, "system");

        // When
        matchDomainService.deleteMatch(matchId);

        // Then
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).existsById(matchId);
        verify(matchRepository).deleteById(matchId);
        verify(eventPublisher).publishMatchDeleted(matchId, "system");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent match")
    void shouldThrowExceptionWhenDeletingNonExistentMatch() {
        // Given
        String matchId = "non-existent-match";
        
        when(matchRepository.existsById(matchId)).thenReturn(false);
        doNothing().when(validationService).validateMatchId(matchId);

        // When & Then
        assertThatThrownBy(() -> matchDomainService.deleteMatch(matchId))
            .isInstanceOf(MatchNotFoundException.class);
        
        verify(matchRepository).existsById(matchId);
        verify(matchRepository, never()).deleteById(anyString());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("Should check match existence correctly")
    void shouldCheckMatchExistenceCorrectly() {
        // Given
        String existingMatchId = "existing-match";
        String nonExistentMatchId = "non-existent-match";
        
        when(matchRepository.existsById(existingMatchId)).thenReturn(true);
        when(matchRepository.existsById(nonExistentMatchId)).thenReturn(false);

        // When & Then
        assertThat(matchDomainService.matchExists(existingMatchId)).isTrue();
        assertThat(matchDomainService.matchExists(nonExistentMatchId)).isFalse();
        assertThat(matchDomainService.matchExists(null)).isFalse();
        assertThat(matchDomainService.matchExists("")).isFalse();
        assertThat(matchDomainService.matchExists("   ")).isFalse();
    }

    @Test
    @DisplayName("Should update match successfully")
    void shouldUpdateMatchSuccessfully() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        
        when(matchRepository.save(match)).thenReturn(match);
        doNothing().when(validationService).validateMatchId(match.getMatchId());

        // When
        Match result = matchDomainService.updateMatch(match);

        // Then
        assertThat(result).isEqualTo(match);
        
        verify(validationService).validateMatchId(match.getMatchId());
        verify(matchRepository).save(match);
    }

    @Test
    @DisplayName("Should throw exception when updating null match")
    void shouldThrowExceptionWhenUpdatingNullMatch() {
        // When & Then
        assertThatThrownBy(() -> matchDomainService.updateMatch(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Match cannot be null");
        
        verifyNoInteractions(validationService, matchRepository);
    }

    @Test
    @DisplayName("Should get current score correctly")
    void shouldGetCurrentScoreCorrectly() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        String expectedScore = "0-0 (0-0)";
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(scoringService.calculateCurrentScore(match)).thenReturn(expectedScore);
        doNothing().when(validationService).validateMatchId(matchId);

        // When
        String result = matchDomainService.getCurrentScore(matchId);

        // Then
        assertThat(result).isEqualTo(expectedScore);
        
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).findById(matchId);
        verify(scoringService).calculateCurrentScore(match);
    }

    @Test
    @DisplayName("Should check deuce status correctly")
    void shouldCheckDeuceStatusCorrectly() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(scoringService.isCurrentGameDeuce(match)).thenReturn(true);
        doNothing().when(validationService).validateMatchId(matchId);

        // When
        boolean result = matchDomainService.isMatchInDeuce(matchId);

        // Then
        assertThat(result).isTrue();
        
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).findById(matchId);
        verify(scoringService).isCurrentGameDeuce(match);
    }

    @Test
    @DisplayName("Should check player advantage correctly")
    void shouldCheckPlayerAdvantageCorrectly() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        String playerId = match.getPlayer1().getPlayerId().getValue();
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(scoringService.hasAdvantage(eq(match), any(PlayerId.class))).thenReturn(true);
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(validationService).validatePlayerId(playerId);

        // When
        boolean result = matchDomainService.playerHasAdvantage(matchId, playerId);

        // Then
        assertThat(result).isTrue();
        
        verify(validationService).validateMatchId(matchId);
        verify(validationService).validatePlayerId(playerId);
        verify(matchRepository).findById(matchId);
        verify(scoringService).hasAdvantage(eq(match), any(PlayerId.class));
    }

    @Test
    @DisplayName("Should check tiebreak status correctly")
    void shouldCheckTiebreakStatusCorrectly() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(scoringService.isCurrentGameTiebreak(match)).thenReturn(true);
        doNothing().when(validationService).validateMatchId(matchId);

        // When
        boolean result = matchDomainService.isCurrentGameTiebreak(matchId);

        // Then
        assertThat(result).isTrue();
        
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).findById(matchId);
        verify(scoringService).isCurrentGameTiebreak(match);
    }

    @Test
    @DisplayName("Should cancel match successfully")
    void shouldCancelMatchSuccessfully() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.save(match)).thenReturn(match);
        doNothing().when(validationService).validateMatchId(matchId);
        doNothing().when(validationService).validateMatchStateForCancellation(match);
        doNothing().when(eventPublisher).publishMatchDeleted(matchId, "cancelled");

        // When
        Match result = matchDomainService.cancelMatch(matchId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isCancelled()).isTrue();
        
        verify(validationService).validateMatchId(matchId);
        verify(validationService).validateMatchStateForCancellation(match);
        verify(matchRepository).findById(matchId);
        verify(matchRepository).save(match);
        verify(eventPublisher).publishMatchDeleted(matchId, "cancelled");
    }

    @Test
    @DisplayName("Should get match statistics correctly")
    void shouldGetMatchStatisticsCorrectly() {
        // Given
        String matchId = "123e4567-e89b-12d3-a456-426614174000";
        Match match = Match.create("John Doe", "Jane Smith");
        
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        doNothing().when(validationService).validateMatchId(matchId);

        // When
        MatchDomainService.MatchStatistics result = matchDomainService.getMatchStatistics(matchId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMatchId()).isEqualTo(match.getMatchId());
        assertThat(result.getPlayer1Name()).isEqualTo("John Doe");
        assertThat(result.getPlayer2Name()).isEqualTo("Jane Smith");
        assertThat(result.getStatus()).isEqualTo(MatchStatus.IN_PROGRESS);
        
        verify(validationService).validateMatchId(matchId);
        verify(matchRepository).findById(matchId);
    }
}