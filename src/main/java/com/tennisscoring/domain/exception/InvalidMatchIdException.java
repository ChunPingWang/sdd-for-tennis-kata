package com.tennisscoring.domain.exception;

/**
 * Exception thrown when a match ID is invalid.
 */
public class InvalidMatchIdException extends ValidationException {
    
    public InvalidMatchIdException(String message) {
        super(message);
    }
    
    public InvalidMatchIdException(String message, Throwable cause) {
        super(message, cause);
    }
}