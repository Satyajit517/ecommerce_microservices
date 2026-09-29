package com.project.UserService.Exception;

public class InvalidInventoryOperationException extends BusinessException{

    public InvalidInventoryOperationException(String message)
    {
        super(message, "INVALID_INVENTORY_OPERATION");
    }
}
