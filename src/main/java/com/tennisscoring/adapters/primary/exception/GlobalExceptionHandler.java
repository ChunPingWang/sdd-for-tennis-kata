package com.tennisscoring.adapters.primary.exception;

import com.tennisscoring.adapters.primary.dto.response.ErrorResponse;
import com.tennisscoring.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for REST API controllers.
 * REST API 控制器的全域異常處理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle validation errors from @Valid annotations.
     * 處理來自 @Valid 註解的驗證錯誤
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());
        
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Validation Failed",
                "請求參數驗證失敗",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                details
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle type mismatch errors (e.g., invalid path variables).
     * 處理類型不匹配錯誤（例如無效的路徑變數）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        logger.warn("Type mismatch error: {}", ex.getMessage());
        
        String message = String.format("參數 '%s' 的值 '%s' 無效", 
                ex.getName(), ex.getValue());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid Parameter",
                message,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle match not found exceptions.
     * 處理比賽不存在異常
     */
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMatchNotFoundException(
            MatchNotFoundException ex, HttpServletRequest request) {
        
        logger.warn("Match not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Match Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle invalid player name exceptions.
     * 處理無效球員名稱異常
     */
    @ExceptionHandler(InvalidPlayerNameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPlayerNameException(
            InvalidPlayerNameException ex, HttpServletRequest request) {
        
        logger.warn("Invalid player name: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid Player Name",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle duplicate player exceptions.
     * 處理重複球員異常
     */
    @ExceptionHandler(DuplicatePlayerException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePlayerException(
            DuplicatePlayerException ex, HttpServletRequest request) {
        
        logger.warn("Duplicate player: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Duplicate Player",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handle invalid player exceptions.
     * 處理無效球員異常
     */
    @ExceptionHandler(InvalidPlayerException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPlayerException(
            InvalidPlayerException ex, HttpServletRequest request) {
        
        logger.warn("Invalid player: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid Player",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle match completed exceptions.
     * 處理比賽已完成異常
     */
    @ExceptionHandler(MatchCompletedException.class)
    public ResponseEntity<ErrorResponse> handleMatchCompletedException(
            MatchCompletedException ex, HttpServletRequest request) {
        
        logger.warn("Match completed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Match Completed",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handle invalid match ID exceptions.
     * 處理無效比賽ID異常
     */
    @ExceptionHandler(InvalidMatchIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMatchIdException(
            InvalidMatchIdException ex, HttpServletRequest request) {
        
        logger.warn("Invalid match ID: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid Match ID",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle illegal argument exceptions.
     * 處理非法參數異常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid Argument",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle illegal state exceptions.
     * 處理非法狀態異常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        
        logger.warn("Illegal state: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid State",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handle all other unexpected exceptions.
     * 處理所有其他未預期的異常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Internal Server Error",
                "系統發生未預期的錯誤，請稍後再試",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle runtime exceptions.
     * 處理運行時異常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        logger.error("Runtime error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Runtime Error",
                "系統運行時發生錯誤：" + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}