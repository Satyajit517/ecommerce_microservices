package com.project.OrderService.Exception;

public class InvalidInventoryOperationException extends BusinessException{

    public InvalidInventoryOperationException(String message)
    {
        super(message, "INVALID_INVENTORY_OPERATION");
    }
}
