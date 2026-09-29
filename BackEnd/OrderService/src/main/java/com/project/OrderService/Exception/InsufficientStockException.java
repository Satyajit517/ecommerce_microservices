package com.project.OrderService.Exception;

public class InsufficientStockException extends BusinessException{

    public InsufficientStockException(String message)
    {
        super(message, "INSUFFICIENT_STOCK");
    }
}
