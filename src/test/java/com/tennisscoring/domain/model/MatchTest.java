package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Match Entity Tests")
class MatchTest {

    @Test
    @DisplayName("Should create match with correct initial state")
    void shouldCreateMatchWithCorrectInitialState() {
        // When
        Match match = Match.create("John Doe", "Jane Smith");
        
        // Then
        assertThat(match.getMatchId()).isNotNull();
        assertThat(match.getPlayer1().getName()).isEqualTo("John Doe");
        assertThat(match.getPlayer2().getName()).isEqualTo("Jane Smith");
        assertThat(match.getStatus()).isEqualTo(MatchStatus.IN_PROGRESS);
        assertThat(match.isInProgress()).isTrue();
        assertThat(match.isCompleted()).isFalse();
        assertThat(match.isCancelled()).isFalse();
        assertThat(match.getCreatedAt()).isNotNull();
        assertThat(match.getCompletedAt()).isNull();
        assertThat(match.getWinner()).isNull();
        assertThat(match.getSets()).hasSize(1);
        assertThat(match.getCurrentSetNumber()).isEqualTo(1);
        assertThat(match.getCurrentGameNumber()).isEqualTo(1);
        assertThat(match.isCurrentGameTiebreak()).isFalse();
    }

    @Test
    @DisplayName("Should create match with specific IDs")
    void shouldCreateMatchWithSpecificIds() {
        // Given
        MatchId matchId = MatchId.generate();
        Player player1 = Player.create("John Doe");
        Player player2 = Player.create("Jane Smith");
        
        // When
        Match match = Match.create(matchId, player1, player2);
        
        // Then
        assertThat(match.getMatchIdObject()).isEqualTo(matchId);
        assertThat(match.getPlayer1()).isEqualTo(player1);
        assertThat(match.getPlayer2()).isEqualTo(player2);
    }

    @Test
    @DisplayName("Should throw exception for null parameters")
    void shouldThrowExceptionForNullParameters() {
        // Given
        MatchId matchId = MatchId.generate();
        Player player1 = Player.create("John Doe");
        Player player2 = Player.create("Jane Smith");
        
        // When & Then
        assertThatThrownBy(() -> Match.create(null, player1, player2))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Match ID cannot be null");
        
        assertThatThrownBy(() -> Match.create(matchId, null, player2))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Player 1 cannot be null");
        
        assertThatThrownBy(() -> Match.create(matchId, player1, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Player 2 cannot be null");
    }

    @Test
    @DisplayName("Should get player by ID correctly")
    void shouldGetPlayerByIdCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        PlayerId player2Id = match.getPlayer2().getPlayerId();
        
        // When & Then
        assertThat(match.getPlayer(player1Id)).isEqualTo(match.getPlayer1());
        assertThat(match.getPlayer(player2Id)).isEqualTo(match.getPlayer2());
        assertThat(match.getPlayer(player1Id.getValue())).isEqualTo(match.getPlayer1());
        assertThat(match.getPlayer(player2Id.getValue())).isEqualTo(match.getPlayer2());
    }

    @Test
    @DisplayName("Should throw exception for unknown player ID")
    void shouldThrowExceptionForUnknownPlayerId() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId unknownId = PlayerId.generate();
        
        // When & Then
        assertThatThrownBy(() -> match.getPlayer(unknownId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player not found");
        
        assertThatThrownBy(() -> match.getPlayer(unknownId.getValue()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player not found");
    }

    @Test
    @DisplayName("Should score points and update game correctly")
    void shouldScorePointsAndUpdateGameCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        
        // When & Then
        // Score first point
        boolean matchCompleted = match.scorePoint(player1Id);
        assertThat(matchCompleted).isFalse();
        assertThat(match.getPlayer1().getPointsWon()).isEqualTo(1);
        
        // Score more points to win a game
        for (int i = 0; i < 3; i++) {
            matchCompleted = match.scorePoint(player1Id);
            assertThat(matchCompleted).isFalse();
        }
        
        // Player 1 should have won the first game
        assertThat(match.getPlayer1().getPointsWon()).isEqualTo(4);
        assertThat(match.getCurrentGameNumber()).isEqualTo(2); // New game started
    }

    @Test
    @DisplayName("Should complete match when player wins 2 sets")
    void shouldCompleteMatchWhenPlayerWins2Sets() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        
        // When
        // Simulate player 1 winning first set 6-0
        for (int game = 0; game < 6; game++) {
            for (int point = 0; point < 4; point++) {
                match.scorePoint(player1Id);
            }
        }
        
        // Player 1 should have won first set
        assertThat(match.getPlayer1().getSetsWon()).isEqualTo(1);
        assertThat(match.getCurrentSetNumber()).isEqualTo(2);
        
        // Simulate player 1 winning second set 6-0
        for (int game = 0; game < 6; game++) {
            for (int point = 0; point < 4; point++) {
                boolean matchCompleted = match.scorePoint(player1Id);
                if (game == 5 && point == 3) { // Last point of match
                    assertThat(matchCompleted).isTrue();
                }
            }
        }
        
        // Then
        assertThat(match.isCompleted()).isTrue();
        assertThat(match.getStatus()).isEqualTo(MatchStatus.COMPLETED);
        assertThat(match.getWinner()).isEqualTo(player1Id);
        assertThat(match.getWinnerPlayer()).isEqualTo(match.getPlayer1());
        assertThat(match.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when scoring on completed match")
    void shouldThrowExceptionWhenScoringOnCompletedMatch() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        
        // Complete the match
        for (int set = 0; set < 2; set++) {
            for (int game = 0; game < 6; game++) {
                for (int point = 0; point < 4; point++) {
                    match.scorePoint(player1Id);
                }
            }
        }
        
        // When & Then
        assertThatThrownBy(() -> match.scorePoint(player1Id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot score on completed match");
    }

    @Test
    @DisplayName("Should cancel match correctly")
    void shouldCancelMatchCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        
        // When
        match.cancel();
        
        // Then
        assertThat(match.getStatus()).isEqualTo(MatchStatus.CANCELLED);
        assertThat(match.isCancelled()).isTrue();
        assertThat(match.isCompleted()).isFalse();
        assertThat(match.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when cancelling completed match")
    void shouldThrowExceptionWhenCancellingCompletedMatch() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        
        // Complete the match
        for (int set = 0; set < 2; set++) {
            for (int game = 0; game < 6; game++) {
                for (int point = 0; point < 4; point++) {
                    match.scorePoint(player1Id);
                }
            }
        }
        
        // When & Then
        assertThatThrownBy(() -> match.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel completed match");
    }

    @Test
    @DisplayName("Should get current score correctly")
    void shouldGetCurrentScoreCorrectly() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        
        // When & Then
        // Initial score
        String initialScore = match.getCurrentScore();
        assertThat(initialScore).contains("0-0");
        
        // Score some points
        match.scorePoint(player1Id);
        match.scorePoint(player1Id);
        
        String currentScore = match.getCurrentScore();
        assertThat(currentScore).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle tiebreak scenario")
    void shouldHandleTiebreakScenario() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        PlayerId player2Id = match.getPlayer2().getPlayerId();
        
        // When
        // Simulate 6-6 games in first set
        for (int game = 0; game < 6; game++) {
            // Player 1 wins a game
            for (int point = 0; point < 4; point++) {
                match.scorePoint(player1Id);
            }
            // Player 2 wins a game
            for (int point = 0; point < 4; point++) {
                match.scorePoint(player2Id);
            }
        }
        
        // Then
        assertThat(match.isCurrentGameTiebreak()).isTrue();
        assertThat(match.getCurrentSet().shouldStartTiebreak()).isTrue();
    }

    @Test
    @DisplayName("Should be equal when match IDs are the same")
    void shouldBeEqualWhenMatchIdsAreSame() {
        // Given
        MatchId matchId = MatchId.generate();
        Player player1 = Player.create("John Doe");
        Player player2 = Player.create("Jane Smith");
        
        Match match1 = Match.create(matchId, player1, player2);
        Match match2 = Match.create(matchId, Player.create("Different"), Player.create("Names"));
        
        // Then
        assertThat(match1).isEqualTo(match2);
        assertThat(match1.hashCode()).isEqualTo(match2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when match IDs are different")
    void shouldNotBeEqualWhenMatchIdsAreDifferent() {
        // Given
        Match match1 = Match.create("John Doe", "Jane Smith");
        Match match2 = Match.create("John Doe", "Jane Smith");
        
        // Then
        assertThat(match1).isNotEqualTo(match2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        
        // When
        String toString = match.toString();
        
        // Then
        assertThat(toString).contains("John Doe");
        assertThat(toString).contains("Jane Smith");
        assertThat(toString).contains("進行中"); // Chinese display name for IN_PROGRESS
        assertThat(toString).contains("matchId=");
        assertThat(toString).contains("currentScore=");
    }

    @Test
    @DisplayName("Should return null winner when match not completed")
    void shouldReturnNullWinnerWhenMatchNotCompleted() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        
        // When & Then
        assertThat(match.getWinnerPlayer()).isNull();
    }
}