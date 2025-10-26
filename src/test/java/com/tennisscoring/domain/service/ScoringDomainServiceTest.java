package com.tennisscoring.domain.service;

import com.tennisscoring.domain.exception.InvalidMatchStateException;
import com.tennisscoring.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScoringDomainService Unit Tests")
class ScoringDomainServiceTest {

    @Mock
    private ValidationService validationService;

    private ScoringDomainService scoringDomainService;

    @BeforeEach
    void setUp() {
        scoringDomainService = new ScoringDomainService(validationService);
    }

    @Test
    @DisplayName("Should score point successfully in regular game")
    void shouldScorePointSuccessfullyInRegularGame() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId playerId = match.getPlayer1().getPlayerId();
        
        doNothing().when(validationService).validateMatchStateForScoring(match);
        doNothing().when(validationService).validatePlayerInMatch(match, playerId.getValue());

        // When
        boolean matchCompleted = scoringDomainService.scorePoint(match, playerId);

        // Then
        assertThat(matchCompleted).isFalse();
        // The actual scoring is done by the Match.scorePoint method, not the service
        // So we just verify the service was called correctly
        
        verify(validationService).validateMatchStateForScoring(match);
        verify(validationService).validatePlayerInMatch(match, playerId.getValue());
    }

    @Test
    @DisplayName("Should throw exception when scoring on invalid match state")
    void shouldThrowExceptionWhenScoringOnInvalidMatchState() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId playerId = match.getPlayer1().getPlayerId();
        
        doThrow(new InvalidMatchStateException("Cannot score on completed match"))
            .when(validationService).validateMatchStateForScoring(match);

        // When & Then
        assertThatThrownBy(() -> scoringDomainService.scorePoint(match, playerId))
            .isInstanceOf(InvalidMatchStateException.class)
            .hasMessageContaining("Cannot score on completed match");
        
        verify(validationService).validateMatchStateForScoring(match);
        verify(validationService, never()).validatePlayerInMatch(any(), any());
    }

    @Test
    @DisplayName("Should calculate current score correctly")
    void shouldCalculateCurrentScoreCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        String expectedScore = "0-0 (0-0)";

        // When
        String result = scoringDomainService.calculateCurrentScore(match);

        // Then
        assertThat(result).isEqualTo(expectedScore);
    }

    @Test
    @DisplayName("Should return empty string for null match")
    void shouldReturnEmptyStringForNullMatch() {
        // When
        String result = scoringDomainService.calculateCurrentScore(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should check deuce status correctly")
    void shouldCheckDeuceStatusCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When
        boolean result = scoringDomainService.isCurrentGameDeuce(match);

        // Then
        assertThat(result).isFalse(); // New match should not be in deuce
    }

    @Test
    @DisplayName("Should return false for deuce check on null match")
    void shouldReturnFalseForDeuceCheckOnNullMatch() {
        // When
        boolean result = scoringDomainService.isCurrentGameDeuce(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should check advantage status correctly")
    void shouldCheckAdvantageStatusCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId playerId = match.getPlayer1().getPlayerId();

        // When
        boolean result = scoringDomainService.hasAdvantage(match, playerId);

        // Then
        assertThat(result).isFalse(); // New match should not have advantage
    }

    @Test
    @DisplayName("Should return false for advantage check on null match")
    void shouldReturnFalseForAdvantageCheckOnNullMatch() {
        // Given
        PlayerId playerId = PlayerId.generate();

        // When
        boolean result = scoringDomainService.hasAdvantage(null, playerId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false for advantage check on null player")
    void shouldReturnFalseForAdvantageCheckOnNullPlayer() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When
        boolean result = scoringDomainService.hasAdvantage(match, null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should check tiebreak status correctly")
    void shouldCheckTiebreakStatusCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When
        boolean result = scoringDomainService.isCurrentGameTiebreak(match);

        // Then
        assertThat(result).isFalse(); // New match should not be in tiebreak
    }

    @Test
    @DisplayName("Should return false for tiebreak check on null match")
    void shouldReturnFalseForTiebreakCheckOnNullMatch() {
        // When
        boolean result = scoringDomainService.isCurrentGameTiebreak(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should get current game score correctly")
    void shouldGetCurrentGameScoreCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId playerId = match.getPlayer1().getPlayerId();

        // When
        GameScore result = scoringDomainService.getCurrentGameScore(match, playerId);

        // Then
        assertThat(result).isEqualTo(GameScore.LOVE);
    }

    @Test
    @DisplayName("Should return null for game score on null match")
    void shouldReturnNullForGameScoreOnNullMatch() {
        // Given
        PlayerId playerId = PlayerId.generate();

        // When
        GameScore result = scoringDomainService.getCurrentGameScore(null, playerId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null for game score on null player")
    void shouldReturnNullForGameScoreOnNullPlayer() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When
        GameScore result = scoringDomainService.getCurrentGameScore(match, null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should get current tiebreak score correctly")
    void shouldGetCurrentTiebreakScoreCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId playerId = match.getPlayer1().getPlayerId();

        // When
        int result = scoringDomainService.getCurrentTiebreakScore(match, playerId);

        // Then
        assertThat(result).isEqualTo(-1); // Not in tiebreak, should return -1
    }

    @Test
    @DisplayName("Should return -1 for tiebreak score on null match")
    void shouldReturnNegativeOneForTiebreakScoreOnNullMatch() {
        // Given
        PlayerId playerId = PlayerId.generate();

        // When
        int result = scoringDomainService.getCurrentTiebreakScore(null, playerId);

        // Then
        assertThat(result).isEqualTo(-1);
    }

    @Test
    @DisplayName("Should return -1 for tiebreak score on null player")
    void shouldReturnNegativeOneForTiebreakScoreOnNullPlayer() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When
        int result = scoringDomainService.getCurrentTiebreakScore(match, null);

        // Then
        assertThat(result).isEqualTo(-1);
    }

    @Test
    @DisplayName("Should handle exception gracefully in deuce check")
    void shouldHandleExceptionGracefullyInDeuceCheck() {
        // Given
        Match match = mock(Match.class);
        when(match.isCompleted()).thenReturn(false);
        when(match.getCurrentGame()).thenThrow(new RuntimeException("Test exception"));

        // When
        boolean result = scoringDomainService.isCurrentGameDeuce(match);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle exception gracefully in advantage check")
    void shouldHandleExceptionGracefullyInAdvantageCheck() {
        // Given
        Match match = mock(Match.class);
        PlayerId playerId = PlayerId.generate();
        when(match.isCompleted()).thenReturn(false);
        when(match.getCurrentGame()).thenThrow(new RuntimeException("Test exception"));

        // When
        boolean result = scoringDomainService.hasAdvantage(match, playerId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle exception gracefully in game score check")
    void shouldHandleExceptionGracefullyInGameScoreCheck() {
        // Given
        Match match = mock(Match.class);
        PlayerId playerId = PlayerId.generate();
        when(match.isCompleted()).thenReturn(false);
        when(match.getCurrentGame()).thenThrow(new RuntimeException("Test exception"));

        // When
        GameScore result = scoringDomainService.getCurrentGameScore(match, playerId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle exception gracefully in tiebreak score check")
    void shouldHandleExceptionGracefullyInTiebreakScoreCheck() {
        // Given
        Match match = mock(Match.class);
        PlayerId playerId = PlayerId.generate();
        when(match.isCompleted()).thenReturn(false);
        when(match.getCurrentGame()).thenThrow(new RuntimeException("Test exception"));

        // When
        int result = scoringDomainService.getCurrentTiebreakScore(match, playerId);

        // Then
        assertThat(result).isEqualTo(-1);
    }

    @Test
    @DisplayName("Should return false for completed match in deuce check")
    void shouldReturnFalseForCompletedMatchInDeuceCheck() {
        // Given
        Match match = mock(Match.class);
        when(match.isCompleted()).thenReturn(true);

        // When
        boolean result = scoringDomainService.isCurrentGameDeuce(match);

        // Then
        assertThat(result).isFalse();
        verify(match, never()).getCurrentGame();
    }

    @Test
    @DisplayName("Should return false for completed match in advantage check")
    void shouldReturnFalseForCompletedMatchInAdvantageCheck() {
        // Given
        Match match = mock(Match.class);
        PlayerId playerId = PlayerId.generate();
        when(match.isCompleted()).thenReturn(true);

        // When
        boolean result = scoringDomainService.hasAdvantage(match, playerId);

        // Then
        assertThat(result).isFalse();
        verify(match, never()).getCurrentGame();
    }

    @Test
    @DisplayName("Should return null for completed match in game score check")
    void shouldReturnNullForCompletedMatchInGameScoreCheck() {
        // Given
        Match match = mock(Match.class);
        PlayerId playerId = PlayerId.generate();
        when(match.isCompleted()).thenReturn(true);

        // When
        GameScore result = scoringDomainService.getCurrentGameScore(match, playerId);

        // Then
        assertThat(result).isNull();
        verify(match, never()).getCurrentGame();
    }

    @Test
    @DisplayName("Should return -1 for completed match in tiebreak score check")
    void shouldReturnNegativeOneForCompletedMatchInTiebreakScoreCheck() {
        // Given
        Match match = mock(Match.class);
        PlayerId playerId = PlayerId.generate();
        when(match.isCompleted()).thenReturn(true);

        // When
        int result = scoringDomainService.getCurrentTiebreakScore(match, playerId);

        // Then
        assertThat(result).isEqualTo(-1);
        verify(match, never()).getCurrentGame();
    }
}