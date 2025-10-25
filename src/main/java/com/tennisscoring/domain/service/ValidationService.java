package com.tennisscoring.domain.service;

import com.tennisscoring.domain.exception.*;
import com.tennisscoring.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Service responsible for input validation and business rule validation.
 * 負責輸入驗證和業務規則驗證的服務
 * 
 * This service centralizes all validation logic to ensure consistent
 * validation across the application and proper error handling.
 * 
 * Requirements: 9.1, 9.2, 9.5
 */
@Component
public class ValidationService {
    
    // Constants for validation rules
    private static final int MAX_PLAYER_NAME_LENGTH = 50;
    private static final int MIN_PLAYER_NAME_LENGTH = 1;
    private static final Pattern VALID_UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    private static final Pattern VALID_PLAYER_NAME_PATTERN = Pattern.compile("^[\\p{L}\\p{N}\\s\\-\\.]+$");
    
    /**
     * Validate player names for match creation.
     * 驗證創建比賽時的球員姓名
     * 
     * @param player1Name name of player 1
     * @param player2Name name of player 2
     * @throws ValidationException if validation fails
     * @throws DuplicatePlayerException if player names are the same
     */
    public void validatePlayerNames(String player1Name, String player2Name) {
        validatePlayerName(player1Name, "player1Name");
        validatePlayerName(player2Name, "player2Name");
        
        if (player1Name.trim().equalsIgnoreCase(player2Name.trim())) {
            throw new DuplicatePlayerException(player1Name);
        }
    }
    
    /**
     * Validate a single player name.
     * 驗證單個球員姓名
     * 
     * @param playerName the player name to validate
     * @param fieldName the field name for error reporting
     * @throws ValidationException if validation fails
     */
    public void validatePlayerName(String playerName, String fieldName) {
        if (playerName == null) {
            throw new ValidationException(fieldName, null, "Player name cannot be null");
        }
        
        String trimmedName = playerName.trim();
        
        if (trimmedName.isEmpty()) {
            throw new ValidationException(fieldName, playerName, "Player name cannot be empty");
        }
        
        if (trimmedName.length() < MIN_PLAYER_NAME_LENGTH) {
            throw new ValidationException(fieldName, playerName, 
                "Player name must be at least " + MIN_PLAYER_NAME_LENGTH + " character long");
        }
        
        if (trimmedName.length() > MAX_PLAYER_NAME_LENGTH) {
            throw new ValidationException(fieldName, playerName, 
                "Player name cannot exceed " + MAX_PLAYER_NAME_LENGTH + " characters");
        }
        
        if (!VALID_PLAYER_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new ValidationException(fieldName, playerName, 
                "Player name contains invalid characters. Only letters, numbers, spaces, hyphens, and dots are allowed");
        }
    }
    
    /**
     * Validate match ID format and presence.
     * 驗證比賽ID格式和存在性
     * 
     * @param matchId the match ID to validate
     * @throws ValidationException if validation fails
     */
    public void validateMatchId(String matchId) {
        if (matchId == null) {
            throw new ValidationException("matchId", null, "Match ID cannot be null");
        }
        
        String trimmedId = matchId.trim();
        
        if (trimmedId.isEmpty()) {
            throw new ValidationException("matchId", matchId, "Match ID cannot be empty");
        }
        
        if (!VALID_UUID_PATTERN.matcher(trimmedId).matches()) {
            throw new ValidationException("matchId", matchId, "Match ID must be a valid UUID format");
        }
    }
    
    /**
     * Validate player ID format and presence.
     * 驗證球員ID格式和存在性
     * 
     * @param playerId the player ID to validate
     * @throws ValidationException if validation fails
     */
    public void validatePlayerId(String playerId) {
        if (playerId == null) {
            throw new ValidationException("playerId", null, "Player ID cannot be null");
        }
        
        String trimmedId = playerId.trim();
        
        if (trimmedId.isEmpty()) {
            throw new ValidationException("playerId", playerId, "Player ID cannot be empty");
        }
        
        if (!VALID_UUID_PATTERN.matcher(trimmedId).matches()) {
            throw new ValidationException("playerId", playerId, "Player ID must be a valid UUID format");
        }
    }
    
    /**
     * Validate that a match is in a valid state for scoring.
     * 驗證比賽是否處於可以計分的有效狀態
     * 
     * @param match the match to validate
     * @throws InvalidMatchStateException if match state is invalid
     */
    public void validateMatchStateForScoring(Match match) {
        if (match == null) {
            throw new InvalidMatchStateException("Match cannot be null");
        }
        
        if (match.isCompleted()) {
            throw new InvalidMatchStateException("Cannot score on completed match: " + match.getMatchId());
        }
        
        if (match.isCancelled()) {
            throw new InvalidMatchStateException("Cannot score on cancelled match: " + match.getMatchId());
        }
        
        if (!match.isInProgress()) {
            throw new InvalidMatchStateException("Match is not in progress: " + match.getMatchId());
        }
    }
    
    /**
     * Validate that a match is in a valid state for deletion.
     * 驗證比賽是否處於可以刪除的有效狀態
     * 
     * @param match the match to validate
     * @throws InvalidMatchStateException if match cannot be deleted
     */
    public void validateMatchStateForDeletion(Match match) {
        if (match == null) {
            throw new InvalidMatchStateException("Match cannot be null");
        }
        
        // Allow deletion of any match regardless of state
        // This is a business decision - completed matches can be deleted for cleanup
    }
    
    /**
     * Validate that a match is in a valid state for cancellation.
     * 驗證比賽是否處於可以取消的有效狀態
     * 
     * @param match the match to validate
     * @throws InvalidMatchStateException if match cannot be cancelled
     */
    public void validateMatchStateForCancellation(Match match) {
        if (match == null) {
            throw new InvalidMatchStateException("Match cannot be null");
        }
        
        if (match.isCompleted()) {
            throw new InvalidMatchStateException("Cannot cancel completed match: " + match.getMatchId());
        }
        
        if (match.isCancelled()) {
            throw new InvalidMatchStateException("Match is already cancelled: " + match.getMatchId());
        }
    }
    
    /**
     * Validate that a player belongs to a specific match.
     * 驗證球員是否屬於特定比賽
     * 
     * @param match the match to check
     * @param playerId the player ID to validate
     * @throws PlayerNotFoundException if player is not found in the match
     */
    public void validatePlayerInMatch(Match match, String playerId) {
        if (match == null) {
            throw new InvalidMatchStateException("Match cannot be null");
        }
        
        validatePlayerId(playerId);
        
        try {
            match.getPlayer(playerId);
        } catch (IllegalArgumentException e) {
            throw new PlayerNotFoundException(playerId, match.getMatchId());
        }
    }
    
    /**
     * Validate match status parameter.
     * 驗證比賽狀態參數
     * 
     * @param status the match status to validate
     * @throws ValidationException if status is invalid
     */
    public void validateMatchStatus(MatchStatus status) {
        if (status == null) {
            throw new ValidationException("status", null, "Match status cannot be null");
        }
    }
    
    /**
     * Validate that a string is not null or empty.
     * 驗證字串不為null或空白
     * 
     * @param value the string to validate
     * @param fieldName the field name for error reporting
     * @throws ValidationException if validation fails
     */
    public void validateNotNullOrEmpty(String value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, null, fieldName + " cannot be null");
        }
        
        if (value.trim().isEmpty()) {
            throw new ValidationException(fieldName, value, fieldName + " cannot be empty");
        }
    }
    
    /**
     * Validate that an object is not null.
     * 驗證物件不為null
     * 
     * @param value the object to validate
     * @param fieldName the field name for error reporting
     * @throws ValidationException if validation fails
     */
    public void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, null, fieldName + " cannot be null");
        }
    }
    
    /**
     * Validate string length constraints.
     * 驗證字串長度限制
     * 
     * @param value the string to validate
     * @param fieldName the field name for error reporting
     * @param minLength minimum allowed length
     * @param maxLength maximum allowed length
     * @throws ValidationException if validation fails
     */
    public void validateStringLength(String value, String fieldName, int minLength, int maxLength) {
        validateNotNull(value, fieldName);
        
        int length = value.trim().length();
        
        if (length < minLength) {
            throw new ValidationException(fieldName, value, 
                fieldName + " must be at least " + minLength + " characters long");
        }
        
        if (length > maxLength) {
            throw new ValidationException(fieldName, value, 
                fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
    
    /**
     * Check if a string is a valid UUID format.
     * 檢查字串是否為有效的UUID格式
     * 
     * @param value the string to check
     * @return true if valid UUID format, false otherwise
     */
    public boolean isValidUUID(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        return VALID_UUID_PATTERN.matcher(value.trim()).matches();
    }
    
    /**
     * Check if a player name is valid format.
     * 檢查球員姓名是否為有效格式
     * 
     * @param playerName the player name to check
     * @return true if valid format, false otherwise
     */
    public boolean isValidPlayerName(String playerName) {
        if (playerName == null) {
            return false;
        }
        
        String trimmedName = playerName.trim();
        
        return !trimmedName.isEmpty() && 
               trimmedName.length() >= MIN_PLAYER_NAME_LENGTH &&
               trimmedName.length() <= MAX_PLAYER_NAME_LENGTH &&
               VALID_PLAYER_NAME_PATTERN.matcher(trimmedName).matches();
    }
    
    /**
     * Sanitize player name by trimming whitespace.
     * 透過修剪空白字元來清理球員姓名
     * 
     * @param playerName the player name to sanitize
     * @return sanitized player name
     */
    public String sanitizePlayerName(String playerName) {
        if (playerName == null) {
            return null;
        }
        
        return playerName.trim();
    }
    
    /**
     * Sanitize ID by trimming whitespace and converting to lowercase.
     * 透過修剪空白字元並轉換為小寫來清理ID
     * 
     * @param id the ID to sanitize
     * @return sanitized ID
     */
    public String sanitizeId(String id) {
        if (id == null) {
            return null;
        }
        
        return id.trim().toLowerCase();
    }
}