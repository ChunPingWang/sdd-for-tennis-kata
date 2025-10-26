package com.tennisscoring.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Game Entity Tests")
class GameTest {

    private PlayerId player1Id;
    private PlayerId player2Id;

    @BeforeEach
    void setUp() {
        player1Id = PlayerId.generate();
        player2Id = PlayerId.generate();
    }

    @Test
    @DisplayName("Should create regular game with correct initial state")
    void shouldCreateRegularGameWithCorrectInitialState() {
        // When
        Game game = new Game(1, false);
        game.initializeScores(player1Id, player2Id);
        
        // Then
        assertThat(game.getGameNumber()).isEqualTo(1);
        assertThat(game.isTiebreak()).isFalse();
        assertThat(game.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(game.isCompleted()).isFalse();
        assertThat(game.getWinner()).isNull();
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.LOVE);
        assertThat(game.getScore(player2Id)).isEqualTo(GameScore.LOVE);
    }

    @Test
    @DisplayName("Should create tiebreak game with correct initial state")
    void shouldCreateTiebreakGameWithCorrectInitialState() {
        // When
        Game game = new Game(7, true);
        game.initializeScores(player1Id, player2Id);
        
        // Then
        assertThat(game.getGameNumber()).isEqualTo(7);
        assertThat(game.isTiebreak()).isTrue();
        assertThat(game.getTiebreakScore(player1Id)).isZero();
        assertThat(game.getTiebreakScore(player2Id)).isZero();
    }

    @Test
    @DisplayName("Should progress regular game scoring correctly")
    void shouldProgressRegularGameScoringCorrectly() {
        // Given
        Game game = new Game(1, false);
        game.initializeScores(player1Id, player2Id);
        
        // When & Then
        // First point: 15-0
        boolean gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isFalse();
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.FIFTEEN);
        assertThat(game.getScore(player2Id)).isEqualTo(GameScore.LOVE);
        
        // Second point: 30-0
        gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isFalse();
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.THIRTY);
        
        // Third point: 40-0
        gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isFalse();
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.FORTY);
        
        // Fourth point: Game won
        gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isTrue();
        assertThat(game.isCompleted()).isTrue();
        assertThat(game.getWinner()).isEqualTo(player1Id);
    }

    @Test
    @DisplayName("Should handle deuce and advantage correctly")
    void shouldHandleDeuceAndAdvantageCorrectly() {
        // Given
        Game game = new Game(1, false);
        game.initializeScores(player1Id, player2Id);
        
        // Set up deuce situation (40-40)
        game.scorePoint(player1Id, player2Id); // 15-0
        game.scorePoint(player1Id, player2Id); // 30-0
        game.scorePoint(player1Id, player2Id); // 40-0
        game.scorePoint(player2Id, player1Id); // 40-15
        game.scorePoint(player2Id, player1Id); // 40-30
        game.scorePoint(player2Id, player1Id); // 40-40 (deuce)
        
        // When & Then
        // After 40-40, both players should have FORTY score
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.FORTY);
        assertThat(game.getScore(player2Id)).isEqualTo(GameScore.FORTY);
        
        // Player 1 gets advantage
        boolean gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isFalse();
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.ADVANTAGE);
        assertThat(game.hasAdvantage(player1Id)).isTrue();
        
        // Player 2 equalizes back to deuce
        gameCompleted = game.scorePoint(player2Id, player1Id);
        assertThat(gameCompleted).isFalse();
        assertThat(game.getStatus()).isEqualTo(GameStatus.DEUCE);
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.FORTY);
        assertThat(game.getScore(player2Id)).isEqualTo(GameScore.FORTY);
        
        // Player 1 gets advantage again
        gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isFalse();
        assertThat(game.getScore(player1Id)).isEqualTo(GameScore.ADVANTAGE);
        
        // Player 1 wins from advantage
        gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isTrue();
        assertThat(game.isCompleted()).isTrue();
        assertThat(game.getWinner()).isEqualTo(player1Id);
    }

    @Test
    @DisplayName("Should handle tiebreak scoring correctly")
    void shouldHandleTiebreakScoringCorrectly() {
        // Given
        Game game = new Game(7, true);
        game.initializeScores(player1Id, player2Id);
        
        // When & Then
        // Score points up to 6-6
        for (int i = 0; i < 6; i++) {
            boolean gameCompleted = game.scorePoint(player1Id, player2Id);
            assertThat(gameCompleted).isFalse();
            
            gameCompleted = game.scorePoint(player2Id, player1Id);
            assertThat(gameCompleted).isFalse();
        }
        
        assertThat(game.getTiebreakScore(player1Id)).isEqualTo(6);
        assertThat(game.getTiebreakScore(player2Id)).isEqualTo(6);
        
        // Player 1 scores to 7-6 (not enough lead)
        boolean gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isFalse();
        assertThat(game.getTiebreakScore(player1Id)).isEqualTo(7);
        
        // Player 1 scores to 8-6 (wins with 2-point lead)
        gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isTrue();
        assertThat(game.isCompleted()).isTrue();
        assertThat(game.getWinner()).isEqualTo(player1Id);
    }

    @Test
    @DisplayName("Should handle tiebreak win at 7-5")
    void shouldHandleTiebreakWinAt7To5() {
        // Given
        Game game = new Game(7, true);
        game.initializeScores(player1Id, player2Id);
        
        // When & Then
        // Score to 5-5
        for (int i = 0; i < 5; i++) {
            game.scorePoint(player1Id, player2Id);
            game.scorePoint(player2Id, player1Id);
        }
        
        // Player 1 scores to 6-5
        game.scorePoint(player1Id, player2Id);
        assertThat(game.getTiebreakScore(player1Id)).isEqualTo(6);
        
        // Player 1 scores to 7-5 (wins)
        boolean gameCompleted = game.scorePoint(player1Id, player2Id);
        assertThat(gameCompleted).isTrue();
        assertThat(game.isCompleted()).isTrue();
        assertThat(game.getWinner()).isEqualTo(player1Id);
    }

    @Test
    @DisplayName("Should throw exception when scoring on completed game")
    void shouldThrowExceptionWhenScoringOnCompletedGame() {
        // Given
        Game game = new Game(1, false);
        game.initializeScores(player1Id, player2Id);
        
        // Complete the game
        game.scorePoint(player1Id, player2Id); // 15-0
        game.scorePoint(player1Id, player2Id); // 30-0
        game.scorePoint(player1Id, player2Id); // 40-0
        game.scorePoint(player1Id, player2Id); // Game won
        
        // When & Then
        assertThatThrownBy(() -> game.scorePoint(player1Id, player2Id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot score on completed game");
    }

    @Test
    @DisplayName("Should throw exception when using wrong score method for game type")
    void shouldThrowExceptionWhenUsingWrongScoreMethodForGameType() {
        // Given
        Game regularGame = new Game(1, false);
        regularGame.initializeScores(player1Id, player2Id);
        
        Game tiebreakGame = new Game(7, true);
        tiebreakGame.initializeScores(player1Id, player2Id);
        
        // When & Then
        assertThatThrownBy(() -> regularGame.getTiebreakScore(player1Id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Use getScore for regular games");
        
        assertThatThrownBy(() -> tiebreakGame.getScore(player1Id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Use getTiebreakScore for tiebreak games");
    }

    @Test
    @DisplayName("Should format regular game score correctly")
    void shouldFormatRegularGameScoreCorrectly() {
        // Given
        Game game = new Game(1, false);
        game.initializeScores(player1Id, player2Id);
        
        // When & Then
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("0-0");
        
        game.scorePoint(player1Id, player2Id);
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("15-0");
        
        game.scorePoint(player2Id, player1Id);
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("15-15");
    }

    @Test
    @DisplayName("Should format deuce and advantage correctly")
    void shouldFormatDeuceAndAdvantageCorrectly() {
        // Given
        Game game = new Game(1, false);
        game.initializeScores(player1Id, player2Id);
        
        // Set up deuce
        game.scorePoint(player1Id, player2Id); // 15-0
        game.scorePoint(player1Id, player2Id); // 30-0
        game.scorePoint(player1Id, player2Id); // 40-0
        game.scorePoint(player2Id, player1Id); // 40-15
        game.scorePoint(player2Id, player1Id); // 40-30
        game.scorePoint(player2Id, player1Id); // 40-40
        
        // When & Then
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("40-40");
        
        // Player 1 advantage
        game.scorePoint(player1Id, player2Id);
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("AD-40");
        
        // Back to deuce
        game.scorePoint(player2Id, player1Id);
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("平分");
        
        // Player 2 advantage
        game.scorePoint(player2Id, player1Id);
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("40-AD");
    }

    @Test
    @DisplayName("Should format tiebreak score correctly")
    void shouldFormatTiebreakScoreCorrectly() {
        // Given
        Game game = new Game(7, true);
        game.initializeScores(player1Id, player2Id);
        
        // When & Then
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("0-0");
        
        game.scorePoint(player1Id, player2Id);
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("1-0");
        
        game.scorePoint(player2Id, player1Id);
        assertThat(game.getFormattedScore(player1Id, player2Id)).isEqualTo("1-1");
    }

    @Test
    @DisplayName("Should be equal when game numbers and tiebreak status are the same")
    void shouldBeEqualWhenGameNumbersAndTiebreakStatusAreSame() {
        // Given
        Game game1 = new Game(1, false);
        Game game2 = new Game(1, false);
        Game game3 = new Game(1, true);
        
        // Then
        assertThat(game1).isEqualTo(game2);
        assertThat(game1.hashCode()).isEqualTo(game2.hashCode());
        assertThat(game1).isNotEqualTo(game3);
    }
}