package com.project.OrderService.Mapper;

import com.project.OrderService.DTO.Cart.CartItemResponse;
import com.project.OrderService.DTO.Cart.CartResponse;
import com.project.OrderService.DTO.Product.ProductInfoResponse;
import com.project.OrderService.Entity.Cart;
import com.project.OrderService.Entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart, Map<UUID, ProductInfoResponse> productMap) {

        CartResponse response = new CartResponse();

        response.setId(cart.getId());
        response.setStatus(cart.getStatus());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(item -> toItemResponse(
                        item, productMap.get(item.getProductId())
                ))
                .toList();

        response.setItems(items);

        response.setTotalItems(
                items.stream()
                        .mapToInt(CartItemResponse::getQuantity)
                        .sum()
        );

        response.setTotalAmount(
                items.stream()
                        .map(CartItemResponse::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        return response;
    }

    private CartItemResponse toItemResponse(CartItem item, ProductInfoResponse product) {

        CartItemResponse response = new CartItemResponse();

        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(product.getName());
        response.setProductSku(product.getSku());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());

        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        response.setSubtotal(subtotal);

        return response;
    }
}