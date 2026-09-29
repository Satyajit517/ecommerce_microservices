package com.project.OrderService.DTO.Cart;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CartItemResponse {

    private UUID id;

    private UUID productId;

    private String productName;

    private String productSku;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}