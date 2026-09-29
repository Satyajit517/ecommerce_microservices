package com.project.UserService.Exception;

public class CartException extends BusinessException{

    public CartException(String message)
    {
        super(message, "CART_ERROR");
    }
}
