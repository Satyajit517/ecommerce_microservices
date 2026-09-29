package com.project.OrderService.DTO.Order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemResponse {

    private UUID id;

    private UUID productId;

    private UUID sellerId;

    private String productName;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}