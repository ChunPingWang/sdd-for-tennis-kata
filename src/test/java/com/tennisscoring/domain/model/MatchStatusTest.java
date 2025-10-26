package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MatchStatus Enum Tests")
class MatchStatusTest {

    @Test
    @DisplayName("Should have correct display names")
    void shouldHaveCorrectDisplayNames() {
        assertThat(MatchStatus.IN_PROGRESS.getDisplayName()).isEqualTo("進行中");
        assertThat(MatchStatus.COMPLETED.getDisplayName()).isEqualTo("已完成");
        assertThat(MatchStatus.CANCELLED.getDisplayName()).isEqualTo("已取消");
    }

    @Test
    @DisplayName("Should identify active status correctly")
    void shouldIdentifyActiveStatusCorrectly() {
        assertThat(MatchStatus.IN_PROGRESS.isActive()).isTrue();
        assertThat(MatchStatus.COMPLETED.isActive()).isFalse();
        assertThat(MatchStatus.CANCELLED.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should identify finished status correctly")
    void shouldIdentifyFinishedStatusCorrectly() {
        assertThat(MatchStatus.IN_PROGRESS.isFinished()).isFalse();
        assertThat(MatchStatus.COMPLETED.isFinished()).isTrue();
        assertThat(MatchStatus.CANCELLED.isFinished()).isTrue();
    }

    @Test
    @DisplayName("Should return display name in toString")
    void shouldReturnDisplayNameInToString() {
        assertThat(MatchStatus.IN_PROGRESS.toString()).isEqualTo("進行中");
        assertThat(MatchStatus.COMPLETED.toString()).isEqualTo("已完成");
        assertThat(MatchStatus.CANCELLED.toString()).isEqualTo("已取消");
    }
}