package com.project.OrderService.Exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}