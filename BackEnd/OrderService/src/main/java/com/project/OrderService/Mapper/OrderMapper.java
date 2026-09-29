package com.project.OrderService.Mapper;

import com.project.OrderService.DTO.Order.*;
import com.project.OrderService.Entity.Order;
import com.project.OrderService.Entity.OrderAddress;
import com.project.OrderService.Entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order, OrderAddress orderAddress,
                                    List<OrderItem> orderItems) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        if (orderAddress != null) {
            response.setShippingAddress(
                    toAddressResponse(orderAddress)
            );
        }

        if (orderItems != null) {
            response.setItems(
                    orderItems.stream()
                            .map(this::toItemResponse)
                            .toList()
            );
        }

        return response;
    }

    private OrderAddressResponse toAddressResponse(OrderAddress address) {

        OrderAddressResponse response = new OrderAddressResponse();

        response.setId(address.getId());
        response.setAddressLine(address.getAddressLine());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPostalCode(address.getPostalCode());

        return response;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        OrderItemResponse response = new OrderItemResponse();

        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setSellerId(item.getSellerId());
        response.setProductName(item.getProductName());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setSubtotal(item.getSubtotal());

        return response;
    }
}