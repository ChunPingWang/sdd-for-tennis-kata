package com.tennisscoring.domain.service;

import com.tennisscoring.domain.exception.*;
import com.tennisscoring.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ValidationService Unit Tests")
class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    @Test
    @DisplayName("Should validate player names successfully")
    void shouldValidatePlayerNamesSuccessfully() {
        // Given
        String player1Name = "John Doe";
        String player2Name = "Jane Smith";

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validatePlayerNames(player1Name, player2Name));
    }

    @Test
    @DisplayName("Should throw exception for duplicate player names")
    void shouldThrowExceptionForDuplicatePlayerNames() {
        // Given
        String player1Name = "John Doe";
        String player2Name = "john doe"; // Same name, different case

        // When & Then
        assertThatThrownBy(() -> validationService.validatePlayerNames(player1Name, player2Name))
            .isInstanceOf(DuplicatePlayerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw exception for invalid player names")
    void shouldThrowExceptionForInvalidPlayerNames(String invalidName) {
        // When & Then
        assertThatThrownBy(() -> validationService.validatePlayerName(invalidName, "playerName"))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Should accept single character player name")
    void shouldAcceptSingleCharacterPlayerName() {
        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validatePlayerName("A", "playerName"));
    }

    @Test
    @DisplayName("Should throw exception for null player name")
    void shouldThrowExceptionForNullPlayerName() {
        // When & Then
        assertThatThrownBy(() -> validationService.validatePlayerName(null, "playerName"))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Player name cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for player name too long")
    void shouldThrowExceptionForPlayerNameTooLong() {
        // Given
        String longName = "A".repeat(51);

        // When & Then
        assertThatThrownBy(() -> validationService.validatePlayerName(longName, "playerName"))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("cannot exceed 50 characters");
    }

    @Test
    @DisplayName("Should validate valid UUID match ID")
    void shouldValidateValidUuidMatchId() {
        // Given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validateMatchId(validUuid));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "invalid-uuid", "123", "not-a-uuid-at-all"})
    @DisplayName("Should throw exception for invalid match ID formats")
    void shouldThrowExceptionForInvalidMatchIdFormats(String invalidId) {
        // When & Then
        assertThatThrownBy(() -> validationService.validateMatchId(invalidId))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Should throw exception for null match ID")
    void shouldThrowExceptionForNullMatchId() {
        // When & Then
        assertThatThrownBy(() -> validationService.validateMatchId(null))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Match ID cannot be null");
    }

    @Test
    @DisplayName("Should validate valid UUID player ID")
    void shouldValidateValidUuidPlayerId() {
        // Given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validatePlayerId(validUuid));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "invalid-uuid", "123", "not-a-uuid-at-all"})
    @DisplayName("Should throw exception for invalid player ID formats")
    void shouldThrowExceptionForInvalidPlayerIdFormats(String invalidId) {
        // When & Then
        assertThatThrownBy(() -> validationService.validatePlayerId(invalidId))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Should validate match state for scoring successfully")
    void shouldValidateMatchStateForScoringSuccessfully() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validateMatchStateForScoring(match));
    }

    @Test
    @DisplayName("Should throw exception for null match in scoring validation")
    void shouldThrowExceptionForNullMatchInScoringValidation() {
        // When & Then
        assertThatThrownBy(() -> validationService.validateMatchStateForScoring(null))
            .isInstanceOf(InvalidMatchStateException.class)
            .hasMessageContaining("Match cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for completed match in scoring validation")
    void shouldThrowExceptionForCompletedMatchInScoringValidation() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        // Simulate match completion by scoring enough points
        PlayerId player1Id = match.getPlayer1().getPlayerId();
        
        // Complete 2 sets to finish the match
        for (int set = 0; set < 2; set++) {
            for (int game = 0; game < 6; game++) {
                for (int point = 0; point < 4; point++) {
                    match.scorePoint(player1Id);
                }
            }
        }

        // When & Then
        assertThatThrownBy(() -> validationService.validateMatchStateForScoring(match))
            .isInstanceOf(InvalidMatchStateException.class)
            .hasMessageContaining("Cannot score on completed match");
    }

    @Test
    @DisplayName("Should throw exception for cancelled match in scoring validation")
    void shouldThrowExceptionForCancelledMatchInScoringValidation() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        match.cancel();

        // When & Then
        assertThatThrownBy(() -> validationService.validateMatchStateForScoring(match))
            .isInstanceOf(InvalidMatchStateException.class)
            .hasMessageContaining("Cannot score on cancelled match");
    }

    @Test
    @DisplayName("Should validate match state for cancellation successfully")
    void shouldValidateMatchStateForCancellationSuccessfully() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validateMatchStateForCancellation(match));
    }

    @Test
    @DisplayName("Should throw exception for completed match in cancellation validation")
    void shouldThrowExceptionForCompletedMatchInCancellationValidation() {
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
        assertThatThrownBy(() -> validationService.validateMatchStateForCancellation(match))
            .isInstanceOf(InvalidMatchStateException.class)
            .hasMessageContaining("Cannot cancel completed match");
    }

    @Test
    @DisplayName("Should throw exception for already cancelled match in cancellation validation")
    void shouldThrowExceptionForAlreadyCancelledMatchInCancellationValidation() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        match.cancel();

        // When & Then
        assertThatThrownBy(() -> validationService.validateMatchStateForCancellation(match))
            .isInstanceOf(InvalidMatchStateException.class)
            .hasMessageContaining("Match is already cancelled");
    }

    @Test
    @DisplayName("Should validate player in match successfully")
    void shouldValidatePlayerInMatchSuccessfully() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        String playerId = match.getPlayer1().getPlayerId().getValue();

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validatePlayerInMatch(match, playerId));
    }

    @Test
    @DisplayName("Should throw exception for player not in match")
    void shouldThrowExceptionForPlayerNotInMatch() {
        // Given
        Match match = Match.create("John Doe", "Jane Smith");
        String unknownPlayerId = PlayerId.generate().getValue();

        // When & Then
        assertThatThrownBy(() -> validationService.validatePlayerInMatch(match, unknownPlayerId))
            .isInstanceOf(PlayerNotFoundException.class);
    }

    @Test
    @DisplayName("Should validate match status successfully")
    void shouldValidateMatchStatusSuccessfully() {
        // Given
        MatchStatus status = MatchStatus.IN_PROGRESS;

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validateMatchStatus(status));
    }

    @Test
    @DisplayName("Should throw exception for null match status")
    void shouldThrowExceptionForNullMatchStatus() {
        // When & Then
        assertThatThrownBy(() -> validationService.validateMatchStatus(null))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Match status cannot be null");
    }

    @Test
    @DisplayName("Should validate not null or empty string successfully")
    void shouldValidateNotNullOrEmptyStringSuccessfully() {
        // Given
        String validString = "valid string";

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validateNotNullOrEmpty(validString, "fieldName"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw exception for null or empty string")
    void shouldThrowExceptionForNullOrEmptyString(String invalidString) {
        // When & Then
        assertThatThrownBy(() -> validationService.validateNotNullOrEmpty(invalidString, "fieldName"))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Should validate string length successfully")
    void shouldValidateStringLengthSuccessfully() {
        // Given
        String validString = "valid";

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            validationService.validateStringLength(validString, "fieldName", 3, 10));
    }

    @Test
    @DisplayName("Should throw exception for string too short")
    void shouldThrowExceptionForStringTooShort() {
        // Given
        String shortString = "ab";

        // When & Then
        assertThatThrownBy(() -> validationService.validateStringLength(shortString, "fieldName", 3, 10))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("must be at least 3 characters long");
    }

    @Test
    @DisplayName("Should throw exception for string too long")
    void shouldThrowExceptionForStringTooLong() {
        // Given
        String longString = "this string is too long";

        // When & Then
        assertThatThrownBy(() -> validationService.validateStringLength(longString, "fieldName", 3, 10))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("cannot exceed 10 characters");
    }

    @Test
    @DisplayName("Should check valid UUID format correctly")
    void shouldCheckValidUuidFormatCorrectly() {
        // Given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";
        String invalidUuid = "invalid-uuid";

        // When & Then
        assertThat(validationService.isValidUUID(validUuid)).isTrue();
        assertThat(validationService.isValidUUID(invalidUuid)).isFalse();
        assertThat(validationService.isValidUUID(null)).isFalse();
        assertThat(validationService.isValidUUID("")).isFalse();
    }

    @Test
    @DisplayName("Should check valid player name format correctly")
    void shouldCheckValidPlayerNameFormatCorrectly() {
        // Given
        String validName = "John Doe";
        String invalidName = "";
        String tooLongName = "A".repeat(51);

        // When & Then
        assertThat(validationService.isValidPlayerName(validName)).isTrue();
        assertThat(validationService.isValidPlayerName(invalidName)).isFalse();
        assertThat(validationService.isValidPlayerName(tooLongName)).isFalse();
        assertThat(validationService.isValidPlayerName(null)).isFalse();
    }

    @Test
    @DisplayName("Should sanitize player name correctly")
    void shouldSanitizePlayerNameCorrectly() {
        // Given
        String nameWithWhitespace = "  John Doe  ";
        String nullName = null;

        // When & Then
        assertThat(validationService.sanitizePlayerName(nameWithWhitespace)).isEqualTo("John Doe");
        assertThat(validationService.sanitizePlayerName(nullName)).isNull();
    }

    @Test
    @DisplayName("Should sanitize ID correctly")
    void shouldSanitizeIdCorrectly() {
        // Given
        String idWithWhitespace = "  ABC-123  ";
        String nullId = null;

        // When & Then
        assertThat(validationService.sanitizeId(idWithWhitespace)).isEqualTo("abc-123");
        assertThat(validationService.sanitizeId(nullId)).isNull();
    }
}