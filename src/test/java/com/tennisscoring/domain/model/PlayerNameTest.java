package com.tennisscoring.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PlayerName Value Object Tests")
class PlayerNameTest {

    @Test
    @DisplayName("Should create PlayerName with valid name")
    void shouldCreatePlayerNameWithValidName() {
        // Given
        String validName = "John Doe";
        
        // When
        PlayerName playerName = PlayerName.of(validName);
        
        // Then
        assertThat(playerName.getValue()).isEqualTo(validName);
        assertThat(playerName.getDisplayName()).isEqualTo(validName);
    }

    @Test
    @DisplayName("Should trim whitespace from name")
    void shouldTrimWhitespaceFromName() {
        // Given
        String nameWithWhitespace = "  John Doe  ";
        
        // When
        PlayerName playerName = PlayerName.of(nameWithWhitespace);
        
        // Then
        assertThat(playerName.getValue()).isEqualTo("John Doe");
        assertThat(playerName.getDisplayName()).isEqualTo("John Doe");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", " "})
    @DisplayName("Should throw exception for null or empty names")
    void shouldThrowExceptionForNullOrEmptyNames(String invalidName) {
        // When & Then
        assertThatThrownBy(() -> PlayerName.of(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for null name")
    void shouldThrowExceptionForNullName() {
        // When & Then
        assertThatThrownBy(() -> PlayerName.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception for name exceeding 50 characters")
    void shouldThrowExceptionForNameExceeding50Characters() {
        // Given
        String longName = "A".repeat(51);
        
        // When & Then
        assertThatThrownBy(() -> PlayerName.of(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player name cannot exceed 50 characters");
    }

    @Test
    @DisplayName("Should throw exception for name shorter than 2 characters")
    void shouldThrowExceptionForNameShorterThan2Characters() {
        // Given
        String shortName = "A";
        
        // When & Then
        assertThatThrownBy(() -> PlayerName.of(shortName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player name must be at least 2 characters long");
    }

    @Test
    @DisplayName("Should accept name with exactly 50 characters")
    void shouldAcceptNameWithExactly50Characters() {
        // Given
        String maxLengthName = "A".repeat(50);
        
        // When
        PlayerName playerName = PlayerName.of(maxLengthName);
        
        // Then
        assertThat(playerName.getValue()).isEqualTo(maxLengthName);
    }

    @Test
    @DisplayName("Should accept name with exactly 2 characters")
    void shouldAcceptNameWithExactly2Characters() {
        // Given
        String minLengthName = "AB";
        
        // When
        PlayerName playerName = PlayerName.of(minLengthName);
        
        // Then
        assertThat(playerName.getValue()).isEqualTo(minLengthName);
    }

    @Test
    @DisplayName("Should be equal when values are the same")
    void shouldBeEqualWhenValuesAreSame() {
        // Given
        String name = "John Doe";
        PlayerName playerName1 = PlayerName.of(name);
        PlayerName playerName2 = PlayerName.of(name);
        
        // Then
        assertThat(playerName1).isEqualTo(playerName2);
        assertThat(playerName1.hashCode()).isEqualTo(playerName2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when values are different")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        PlayerName playerName1 = PlayerName.of("John Doe");
        PlayerName playerName2 = PlayerName.of("Jane Smith");
        
        // Then
        assertThat(playerName1).isNotEqualTo(playerName2);
    }
}