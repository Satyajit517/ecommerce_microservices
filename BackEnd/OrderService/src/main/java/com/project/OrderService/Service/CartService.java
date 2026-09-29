package com.project.OrderService.Service;

import com.project.OrderService.DTO.Cart.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCart(UUID buyerId);

    CartResponse addItem(
            UUID buyerId,
            UUID productId,
            int quantity
    );

    CartResponse updateItem(
            UUID buyerId,
            UUID productId,
            int quantity
    );

    CartResponse removeItem(
            UUID buyerId,
            UUID productId
    );

    void clearCart(UUID buyerId);

}