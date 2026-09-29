package com.project.ProductService.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse {

    private boolean success;

    private String message;

    private Map<String, String> errors;

    private Instant timestamp;

    public ValidationErrorResponse(
            String message,
            Map<String, String> errors
    ) {
        this.success = false;
        this.message = message;
        this.errors = errors;
        this.timestamp = Instant.now();
    }
}