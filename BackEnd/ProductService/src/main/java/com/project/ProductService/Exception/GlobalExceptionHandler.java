package com.project.ProductService.Exception;

import com.project.ProductService.DTO.ErrorResponse;
import com.project.ProductService.DTO.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler{

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex)
  {
    ErrorResponse response = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            Instant.now()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex)
  {
    ErrorResponse response = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            Instant.now()
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex)
  {
    ErrorResponse response = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value(),
            Instant.now()
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex)
  {
    ErrorResponse response = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.UNAUTHORIZED.value(),
            Instant.now()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

//  @ExceptionHandler(BadRequestException.class)
//  public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex)
//  {
//    ErrorResponse response = new ErrorResponse(
//            ex.getMessage(),
//            HttpStatus.BAD_REQUEST.value(),
//            Instant.now()
//    );
//
//    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidation(
          MethodArgumentNotValidException ex
  ) {

    Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> error.getDefaultMessage(),
                    (existing, replacement) -> existing
            ));

    ValidationErrorResponse response =
            new ValidationErrorResponse(
                    "Validation failed",
                    errors
            );

    return ResponseEntity
            .badRequest()
            .body(response);
  }
}
