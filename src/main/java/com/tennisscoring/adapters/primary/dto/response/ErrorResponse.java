package com.tennisscoring.adapters.primary.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response model for error information.
 * 錯誤資訊的回應模型
 */
public class ErrorResponse {
    
    private String error;
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private List<String> details;
    
    /**
     * Default constructor for JSON serialization.
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with basic error information.
     * 
     * @param error error type
     * @param message error message
     * @param status HTTP status code
     * @param path request path
     */
    public ErrorResponse(String error, String message, int status, String path) {
        this();
        this.error = error;
        this.message = message;
        this.status = status;
        this.path = path;
    }
    
    /**
     * Constructor with detailed error information.
     * 
     * @param error error type
     * @param message error message
     * @param status HTTP status code
     * @param path request path
     * @param details detailed error messages
     */
    public ErrorResponse(String error, String message, int status, String path, List<String> details) {
        this(error, message, status, path);
        this.details = details;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<String> getDetails() {
        return details;
    }
    
    public void setDetails(List<String> details) {
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                ", details=" + details +
                '}';
    }
}