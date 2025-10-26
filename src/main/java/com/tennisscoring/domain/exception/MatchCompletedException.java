package com.tennisscoring.domain.exception;

/**
 * Exception thrown when trying to perform operations on a completed match.
 */
public class MatchCompletedException extends InvalidMatchStateException {
    
    public MatchCompletedException(String message) {
        super(message);
    }
    
    public MatchCompletedException(String message, Throwable cause) {
        super(message, cause);
    }
}