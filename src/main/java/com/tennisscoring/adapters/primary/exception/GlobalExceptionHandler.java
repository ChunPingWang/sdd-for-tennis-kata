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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

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
     * Handle HTTP message not readable exceptions (malformed JSON, missing request body).
     * 處理 HTTP 訊息不可讀異常（格式錯誤的 JSON、缺少請求體）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        logger.warn("HTTP message not readable: {}", ex.getMessage());
        
        String message = "請求格式錯誤或缺少請求體";
        if (ex.getMessage().contains("Required request body is missing")) {
            message = "缺少必要的請求體";
        } else if (ex.getMessage().contains("JSON parse error")) {
            message = "JSON 格式錯誤";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Bad Request",
                message,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle unsupported media type exceptions.
     * 處理不支援的媒體類型異常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        
        logger.warn("Unsupported media type: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Unsupported Media Type",
                "不支援的媒體類型：" + ex.getContentType(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }
    
    /**
     * Handle unsupported HTTP method exceptions.
     * 處理不支援的 HTTP 方法異常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        logger.warn("Method not allowed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Method Not Allowed",
                "不支援的 HTTP 方法：" + ex.getMethod(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
    
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
     * Handle validation exceptions from domain services.
     * 處理來自領域服務的驗證異常
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        
        logger.warn("Domain validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Validation Error",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle player not found exceptions.
     * 處理球員不存在異常
     */
    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlayerNotFoundException(
            PlayerNotFoundException ex, HttpServletRequest request) {
        
        logger.warn("Player not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Player Not Found",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle invalid match state exceptions.
     * 處理無效比賽狀態異常
     */
    @ExceptionHandler(InvalidMatchStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMatchStateException(
            InvalidMatchStateException ex, HttpServletRequest request) {
        
        logger.warn("Invalid match state: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid Match State",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
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
}