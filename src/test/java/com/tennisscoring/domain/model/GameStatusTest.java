package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GameStatus Enum Tests")
class GameStatusTest {

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        assertThat(GameStatus.IN_PROGRESS.getDisplayName()).isEqualTo("進行中");
        assertThat(GameStatus.DEUCE.getDisplayName()).isEqualTo("平分");
        assertThat(GameStatus.ADVANTAGE_PLAYER1.getDisplayName()).isEqualTo("球員1優勢");
        assertThat(GameStatus.ADVANTAGE_PLAYER2.getDisplayName()).isEqualTo("球員2優勢");
        assertThat(GameStatus.COMPLETED.getDisplayName()).isEqualTo("已完成");
    }

    @Test
    @DisplayName("Should identify active status correctly")
    void shouldIdentifyActiveStatusCorrectly() {
        assertThat(GameStatus.IN_PROGRESS.isActive()).isTrue();
        assertThat(GameStatus.DEUCE.isActive()).isTrue();
        assertThat(GameStatus.ADVANTAGE_PLAYER1.isActive()).isTrue();
        assertThat(GameStatus.ADVANTAGE_PLAYER2.isActive()).isTrue();
        assertThat(GameStatus.COMPLETED.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should identify deuce or advantage status correctly")
    void shouldIdentifyDeuceOrAdvantageStatusCorrectly() {
        assertThat(GameStatus.IN_PROGRESS.isDeuceOrAdvantage()).isFalse();
        assertThat(GameStatus.DEUCE.isDeuceOrAdvantage()).isTrue();
        assertThat(GameStatus.ADVANTAGE_PLAYER1.isDeuceOrAdvantage()).isTrue();
        assertThat(GameStatus.ADVANTAGE_PLAYER2.isDeuceOrAdvantage()).isTrue();
        assertThat(GameStatus.COMPLETED.isDeuceOrAdvantage()).isFalse();
    }

    @Test
    @DisplayName("Should return display name in toString")
    void shouldReturnDisplayNameInToString() {
        assertThat(GameStatus.IN_PROGRESS.toString()).isEqualTo("進行中");
        assertThat(GameStatus.DEUCE.toString()).isEqualTo("平分");
        assertThat(GameStatus.ADVANTAGE_PLAYER1.toString()).isEqualTo("球員1優勢");
        assertThat(GameStatus.ADVANTAGE_PLAYER2.toString()).isEqualTo("球員2優勢");
        assertThat(GameStatus.COMPLETED.toString()).isEqualTo("已完成");
    }
}