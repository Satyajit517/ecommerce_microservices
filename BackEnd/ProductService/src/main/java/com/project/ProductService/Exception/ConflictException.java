package com.project.ProductService.Exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}