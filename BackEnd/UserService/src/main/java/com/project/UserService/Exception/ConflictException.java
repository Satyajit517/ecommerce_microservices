package com.project.UserService.Exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}