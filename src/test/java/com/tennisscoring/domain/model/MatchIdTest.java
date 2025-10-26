package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MatchId Value Object Tests")
class MatchIdTest {

    @Test
    @DisplayName("Should generate valid MatchId with UUID format")
    void shouldGenerateValidMatchId() {
        // When
        MatchId matchId = MatchId.generate();
        
        // Then
        assertThat(matchId).isNotNull();
        assertThat(matchId.getValue()).isNotNull();
        assertThat(matchId.getValue()).matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    @Test
    @DisplayName("Should create MatchId from valid UUID string")
    void shouldCreateMatchIdFromValidUuid() {
        // Given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";
        
        // When
        MatchId matchId = MatchId.of(validUuid);
        
        // Then
        assertThat(matchId.getValue()).isEqualTo(validUuid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "invalid-uuid", "123", "not-a-uuid-at-all"})
    @DisplayName("Should throw exception for invalid UUID formats")
    void shouldThrowExceptionForInvalidUuidFormats(String invalidUuid) {
        // When & Then
        assertThatThrownBy(() -> MatchId.of(invalidUuid))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw exception for null value")
    void shouldThrowExceptionForNullValue() {
        // When & Then
        assertThatThrownBy(() -> MatchId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Match ID cannot be null or empty");
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreSame() {
        // Given
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        MatchId matchId1 = MatchId.of(uuid);
        MatchId matchId2 = MatchId.of(uuid);
        
        // Then
        assertThat(matchId1).isEqualTo(matchId2);
        assertThat(matchId1.hashCode()).isEqualTo(matchId2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        MatchId matchId1 = MatchId.generate();
        MatchId matchId2 = MatchId.generate();
        
        // Then
        assertThat(matchId1).isNotEqualTo(matchId2);
    }
}