package com.example.hitachi.test.exception;

import com.example.hitachi.test.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// @ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGlobalException(Exception ex) {
        // Log the exception for debugging purposes (optional, but recommended)
        // Log the exception for debugging purposes (optional, but recommended)
        // logger.error("An unexpected error occurred: " + ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "An unexpected error occurred. Please try again later.", null));
    }
}
