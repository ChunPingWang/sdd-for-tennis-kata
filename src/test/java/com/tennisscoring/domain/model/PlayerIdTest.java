package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PlayerId Value Object Tests")
class PlayerIdTest {

    @Test
    @DisplayName("Should generate valid PlayerId with UUID format")
    void shouldGenerateValidPlayerId() {
        // When
        PlayerId playerId = PlayerId.generate();
        
        // Then
        assertThat(playerId).isNotNull();
        assertThat(playerId.getValue()).isNotNull();
        assertThat(playerId.getValue()).matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    @Test
    @DisplayName("Should create PlayerId from valid UUID string")
    void shouldCreatePlayerIdFromValidUuid() {
        // Given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";
        
        // When
        PlayerId playerId = PlayerId.of(validUuid);
        
        // Then
        assertThat(playerId.getValue()).isEqualTo(validUuid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "invalid-uuid", "123", "not-a-uuid-at-all"})
    @DisplayName("Should throw exception for invalid UUID formats")
    void shouldThrowExceptionForInvalidUuidFormats(String invalidUuid) {
        // When & Then
        assertThatThrownBy(() -> PlayerId.of(invalidUuid))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw exception for null value")
    void shouldThrowExceptionForNullValue() {
        // When & Then
        assertThatThrownBy(() -> PlayerId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player ID cannot be null or empty");
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreSame() {
        // Given
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        PlayerId playerId1 = PlayerId.of(uuid);
        PlayerId playerId2 = PlayerId.of(uuid);
        
        // Then
        assertThat(playerId1).isEqualTo(playerId2);
        assertThat(playerId1.hashCode()).isEqualTo(playerId2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        PlayerId playerId1 = PlayerId.generate();
        PlayerId playerId2 = PlayerId.generate();
        
        // Then
        assertThat(playerId1).isNotEqualTo(playerId2);
    }
}