package com.tennisscoring.domain.exception;

/**
 * Exception thrown when input validation fails
 * 當輸入驗證失敗時拋出的異常
 */
public class ValidationException extends RuntimeException {
    
    private final String field;
    private final Object value;
    
    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    public ValidationException(String field, Object value, String message) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", field, value, message));
        this.field = field;
        this.value = value;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
    }
    
    public ValidationException(String field, Object value, String message, Throwable cause) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", field, value, message), cause);
        this.field = field;
        this.value = value;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
}