package com.tennisscoring.domain.exception;

/**
 * Exception thrown when an operation is attempted on a match in an invalid state
 * 當在無效狀態的比賽上嘗試操作時拋出的異常
 */
public class InvalidMatchStateException extends RuntimeException {
    
    public InvalidMatchStateException(String message) {
        super(message);
    }
    
    public InvalidMatchStateException(String message, Throwable cause) {
        super(message, cause);
    }
}