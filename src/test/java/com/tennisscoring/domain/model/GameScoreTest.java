package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GameScore Enum Tests")
class GameScoreTest {

    @Test
    @DisplayName("Should have correct numeric and display values")
    void shouldHaveCorrectNumericAndDisplayValues() {
        assertThat(GameScore.LOVE.getNumericValue()).isEqualTo(0);
        assertThat(GameScore.LOVE.getDisplayValue()).isEqualTo("0");
        
        assertThat(GameScore.FIFTEEN.getNumericValue()).isEqualTo(15);
        assertThat(GameScore.FIFTEEN.getDisplayValue()).isEqualTo("15");
        
        assertThat(GameScore.THIRTY.getNumericValue()).isEqualTo(30);
        assertThat(GameScore.THIRTY.getDisplayValue()).isEqualTo("30");
        
        assertThat(GameScore.FORTY.getNumericValue()).isEqualTo(40);
        assertThat(GameScore.FORTY.getDisplayValue()).isEqualTo("40");
        
        assertThat(GameScore.ADVANTAGE.getNumericValue()).isEqualTo(50);
        assertThat(GameScore.ADVANTAGE.getDisplayValue()).isEqualTo("AD");
    }

    @Test
    @DisplayName("Should advance to next score correctly")
    void shouldAdvanceToNextScoreCorrectly() {
        assertThat(GameScore.LOVE.next()).isEqualTo(GameScore.FIFTEEN);
        assertThat(GameScore.FIFTEEN.next()).isEqualTo(GameScore.THIRTY);
        assertThat(GameScore.THIRTY.next()).isEqualTo(GameScore.FORTY);
    }

    @Test
    @DisplayName("Should throw exception when advancing from FORTY")
    void shouldThrowExceptionWhenAdvancingFromForty() {
        assertThatThrownBy(() -> GameScore.FORTY.next())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot advance from FORTY without context");
    }

    @Test
    @DisplayName("Should throw exception when advancing from ADVANTAGE")
    void shouldThrowExceptionWhenAdvancingFromAdvantage() {
        assertThatThrownBy(() -> GameScore.ADVANTAGE.next())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot advance from ADVANTAGE");
    }

    @Test
    @DisplayName("Should determine winning conditions correctly")
    void shouldDetermineWinningConditionsCorrectly() {
        // ADVANTAGE always wins
        assertThat(GameScore.ADVANTAGE.winsAgainst(GameScore.LOVE)).isTrue();
        assertThat(GameScore.ADVANTAGE.winsAgainst(GameScore.FIFTEEN)).isTrue();
        assertThat(GameScore.ADVANTAGE.winsAgainst(GameScore.THIRTY)).isTrue();
        assertThat(GameScore.ADVANTAGE.winsAgainst(GameScore.FORTY)).isTrue();
        
        // FORTY wins against LOVE, FIFTEEN, THIRTY but not FORTY or ADVANTAGE
        assertThat(GameScore.FORTY.winsAgainst(GameScore.LOVE)).isTrue();
        assertThat(GameScore.FORTY.winsAgainst(GameScore.FIFTEEN)).isTrue();
        assertThat(GameScore.FORTY.winsAgainst(GameScore.THIRTY)).isTrue();
        assertThat(GameScore.FORTY.winsAgainst(GameScore.FORTY)).isFalse();
        assertThat(GameScore.FORTY.winsAgainst(GameScore.ADVANTAGE)).isFalse();
        
        // Lower scores don't win
        assertThat(GameScore.LOVE.winsAgainst(GameScore.FORTY)).isFalse();
        assertThat(GameScore.FIFTEEN.winsAgainst(GameScore.FORTY)).isFalse();
        assertThat(GameScore.THIRTY.winsAgainst(GameScore.FORTY)).isFalse();
    }

    @Test
    @DisplayName("Should identify deuce situation correctly")
    void shouldIdentifyDeuceSituationCorrectly() {
        // Deuce occurs when both players have FORTY
        assertThat(GameScore.FORTY.isDeuce(GameScore.FORTY)).isTrue();
        
        // Not deuce in other situations
        assertThat(GameScore.FORTY.isDeuce(GameScore.LOVE)).isFalse();
        assertThat(GameScore.FORTY.isDeuce(GameScore.FIFTEEN)).isFalse();
        assertThat(GameScore.FORTY.isDeuce(GameScore.THIRTY)).isFalse();
        assertThat(GameScore.FORTY.isDeuce(GameScore.ADVANTAGE)).isFalse();
        
        assertThat(GameScore.LOVE.isDeuce(GameScore.FORTY)).isFalse();
        assertThat(GameScore.ADVANTAGE.isDeuce(GameScore.FORTY)).isFalse();
    }

    @Test
    @DisplayName("Should return display value in toString")
    void shouldReturnDisplayValueInToString() {
        assertThat(GameScore.LOVE.toString()).isEqualTo("0");
        assertThat(GameScore.FIFTEEN.toString()).isEqualTo("15");
        assertThat(GameScore.THIRTY.toString()).isEqualTo("30");
        assertThat(GameScore.FORTY.toString()).isEqualTo("40");
        assertThat(GameScore.ADVANTAGE.toString()).isEqualTo("AD");
    }
}