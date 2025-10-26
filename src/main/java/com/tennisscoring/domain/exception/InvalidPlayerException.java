package com.tennisscoring.domain.exception;

/**
 * Exception thrown when a player is invalid.
 */
public class InvalidPlayerException extends ValidationException {
    
    public InvalidPlayerException(String message) {
        super(message);
    }
    
    public InvalidPlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}