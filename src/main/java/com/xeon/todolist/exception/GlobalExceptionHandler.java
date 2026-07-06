package com.xeon.todolist.exception;

import com.xeon.todolist.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception e){
        // catch all server-side error
        log.error("An unexpected error occurred while processing the request", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), 500, e.getMessage(), "Internal Server Error");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e){
        return buildErrorResponse(HttpStatus.NOT_FOUND, LocalDateTime.now(),404, e.getMessage(),"Resource Not Found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e){
        return buildErrorResponse(HttpStatus.FORBIDDEN,LocalDateTime.now(),403,e.getMessage(),"Access Denied");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e){
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401, e.getMessage(), "Unauthorized");
    }

    @ExceptionHandler(DuplicateTaskException.class)
    public ResponseEntity<?> handleDuplicatedTaskException(DuplicateTaskException e){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, LocalDateTime.now(),  400, e.getMessage(), "Bad Request" );
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExistException(UserAlreadyExistException e){
        return buildErrorResponse(HttpStatus.CONFLICT, LocalDateTime.now(), 409, e.getMessage(), "User already exists");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException e){
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401, e.getMessage(), "Unauthorized");
    }

    public ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus httpStatus, LocalDateTime localDateTime, int status, String error, String message){
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(localDateTime, status, error, message));
    }
}
