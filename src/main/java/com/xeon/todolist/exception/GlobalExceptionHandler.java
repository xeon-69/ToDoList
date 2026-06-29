package com.xeon.todolist.exception;

import com.xeon.todolist.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e){
        return buildErrorResponse(HttpStatus.NOT_FOUND, LocalDateTime.now(),404, e.getMessage(),"Resource Not Found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e){
        return buildErrorResponse(HttpStatus.FORBIDDEN,LocalDateTime.now(),403,e.getMessage(),"Access Denied");
    }

    public ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus httpStatus, LocalDateTime localDateTime, int status, String error, String message){
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(localDateTime, status, error, message));
    }
}
