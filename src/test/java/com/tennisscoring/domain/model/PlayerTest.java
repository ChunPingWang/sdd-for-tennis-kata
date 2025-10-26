package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Player Entity Tests")
class PlayerTest {

    @Test
    @DisplayName("Should create player with generated ID")
    void shouldCreatePlayerWithGeneratedId() {
        // Given
        String playerName = "John Doe";
        
        // When
        Player player = Player.create(playerName);
        
        // Then
        assertThat(player.getPlayerId()).isNotNull();
        assertThat(player.getName()).isEqualTo(playerName);
        assertThat(player.getSetsWon()).isZero();
        assertThat(player.getGamesWon()).isZero();
        assertThat(player.getPointsWon()).isZero();
    }

    @Test
    @DisplayName("Should create player with specific ID")
    void shouldCreatePlayerWithSpecificId() {
        // Given
        PlayerId playerId = PlayerId.generate();
        String playerName = "Jane Smith";
        
        // When
        Player player = Player.create(playerId, playerName);
        
        // Then
        assertThat(player.getPlayerId()).isEqualTo(playerId);
        assertThat(player.getName()).isEqualTo(playerName);
        assertThat(player.getPlayerName().getValue()).isEqualTo(playerName);
    }

    @Test
    @DisplayName("Should throw exception for null player ID")
    void shouldThrowExceptionForNullPlayerId() {
        // When & Then
        assertThatThrownBy(() -> new Player(null, PlayerName.of("John")))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Player ID cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null player name")
    void shouldThrowExceptionForNullPlayerName() {
        // Given
        PlayerId playerId = PlayerId.generate();
        
        // When & Then
        assertThatThrownBy(() -> new Player(playerId, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Player name cannot be null");
    }

    @Test
    @DisplayName("Should increment sets won")
    void shouldIncrementSetsWon() {
        // Given
        Player player = Player.create("John Doe");
        
        // When
        player.incrementSetsWon();
        player.incrementSetsWon();
        
        // Then
        assertThat(player.getSetsWon()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should increment games won")
    void shouldIncrementGamesWon() {
        // Given
        Player player = Player.create("John Doe");
        
        // When
        player.incrementGamesWon();
        player.incrementGamesWon();
        player.incrementGamesWon();
        
        // Then
        assertThat(player.getGamesWon()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should increment points won")
    void shouldIncrementPointsWon() {
        // Given
        Player player = Player.create("John Doe");
        
        // When
        player.incrementPointsWon();
        player.incrementPointsWon();
        player.incrementPointsWon();
        player.incrementPointsWon();
        
        // Then
        assertThat(player.getPointsWon()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should reset game statistics")
    void shouldResetGameStatistics() {
        // Given
        Player player = Player.create("John Doe");
        player.incrementGamesWon();
        player.incrementGamesWon();
        player.incrementPointsWon();
        player.incrementSetsWon();
        
        // When
        player.resetGameStats();
        
        // Then
        assertThat(player.getGamesWon()).isZero();
        assertThat(player.getPointsWon()).isEqualTo(1); // Points not reset
        assertThat(player.getSetsWon()).isEqualTo(1); // Sets not reset
    }

    @Test
    @DisplayName("Should reset all statistics")
    void shouldResetAllStatistics() {
        // Given
        Player player = Player.create("John Doe");
        player.incrementSetsWon();
        player.incrementGamesWon();
        player.incrementPointsWon();
        
        // When
        player.resetAllStats();
        
        // Then
        assertThat(player.getSetsWon()).isZero();
        assertThat(player.getGamesWon()).isZero();
        assertThat(player.getPointsWon()).isZero();
    }

    @Test
    @DisplayName("Should determine match win correctly")
    void shouldDetermineMatchWinCorrectly() {
        // Given
        Player player = Player.create("John Doe");
        
        // When & Then
        assertThat(player.hasWonMatch()).isFalse();
        
        player.incrementSetsWon();
        assertThat(player.hasWonMatch()).isFalse();
        
        player.incrementSetsWon();
        assertThat(player.hasWonMatch()).isTrue();
    }

    @Test
    @DisplayName("Should determine set win correctly")
    void shouldDetermineSetWinCorrectly() {
        // Given
        Player player = Player.create("John Doe");
        
        // When & Then
        // Need 6 games and at least 2-game lead
        assertThat(player.hasWonSet(0)).isFalse();
        
        // Set games won to 6
        for (int i = 0; i < 6; i++) {
            player.incrementGamesWon();
        }
        
        assertThat(player.hasWonSet(4)).isTrue(); // 6-4
        assertThat(player.hasWonSet(5)).isFalse(); // 6-5 (need 2-game lead)
        assertThat(player.hasWonSet(6)).isFalse(); // 6-6 (need tiebreak)
    }

    @Test
    @DisplayName("Should determine tiebreak condition correctly")
    void shouldDetermineTiebreakConditionCorrectly() {
        // Given
        Player player = Player.create("John Doe");
        
        // When & Then
        assertThat(player.shouldPlayTiebreak(6)).isFalse();
        
        // Set games won to 6
        for (int i = 0; i < 6; i++) {
            player.incrementGamesWon();
        }
        
        assertThat(player.shouldPlayTiebreak(6)).isTrue(); // 6-6
        assertThat(player.shouldPlayTiebreak(5)).isFalse(); // 6-5
    }

    @Test
    @DisplayName("Should be equal when player IDs are the same")
    void shouldBeEqualWhenPlayerIdsAreSame() {
        // Given
        PlayerId playerId = PlayerId.generate();
        Player player1 = Player.create(playerId, "John Doe");
        Player player2 = Player.create(playerId, "Different Name");
        
        // Then
        assertThat(player1).isEqualTo(player2);
        assertThat(player1.hashCode()).isEqualTo(player2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when player IDs are different")
    void shouldNotBeEqualWhenPlayerIdsAreDifferent() {
        // Given
        Player player1 = Player.create("John Doe");
        Player player2 = Player.create("John Doe");
        
        // Then
        assertThat(player1).isNotEqualTo(player2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        // Given
        Player player = Player.create("John Doe");
        player.incrementSetsWon();
        player.incrementGamesWon();
        player.incrementPointsWon();
        
        // When
        String toString = player.toString();
        
        // Then
        assertThat(toString).contains("John Doe");
        assertThat(toString).contains("setsWon=1");
        assertThat(toString).contains("gamesWon=1");
        assertThat(toString).contains("pointsWon=1");
    }
}