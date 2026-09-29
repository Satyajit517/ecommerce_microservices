package com.project.OrderService.DTO.Order;

import com.project.OrderService.Enum.OrderStatus;
import com.project.OrderService.Enum.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponse {

    private UUID id;

    private String orderNumber;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private PaymentStatus paymentStatus;

    private OrderAddressResponse shippingAddress;

    private List<OrderItemResponse> items;

    private Instant createdAt;

    private Instant updatedAt;
}