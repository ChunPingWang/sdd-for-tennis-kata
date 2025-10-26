package com.tennisscoring.domain.exception;

/**
 * Exception thrown when a player name is invalid.
 */
public class InvalidPlayerNameException extends ValidationException {
    
    public InvalidPlayerNameException(String message) {
        super(message);
    }
    
    public InvalidPlayerNameException(String message, Throwable cause) {
        super(message, cause);
    }
}