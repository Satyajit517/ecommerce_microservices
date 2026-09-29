package com.project.OrderService.Service;

import com.project.OrderService.DTO.Order.CreateOrderRequest;
import com.project.OrderService.DTO.Order.OrderResponse;
import com.project.OrderService.Enum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(
            UUID buyerId,
            CreateOrderRequest request
    );

    OrderResponse getOrder(
            UUID buyerId,
            UUID orderId
    );

    Page<OrderResponse> getOrders(
            UUID buyerId,
            Pageable pageable
    );

    Page<OrderResponse> getOrdersByStatus(
            UUID buyerId,
            OrderStatus status,
            Pageable pageable
    );

    void cancelOrder(
            UUID buyerId,
            UUID orderId
    );
}