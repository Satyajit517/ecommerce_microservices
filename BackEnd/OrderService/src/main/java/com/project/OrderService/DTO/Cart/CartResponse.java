package com.project.OrderService.DTO.Cart;

import com.project.OrderService.Enum.CartStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartResponse {

    private UUID id;

    private CartStatus status;

    private List<CartItemResponse> items;

    private int totalItems;

    private BigDecimal totalAmount;

    private Instant createdAt;

    private Instant updatedAt;
}