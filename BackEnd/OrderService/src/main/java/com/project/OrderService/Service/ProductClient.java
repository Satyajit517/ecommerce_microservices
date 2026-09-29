package com.project.OrderService.Service;

import com.project.OrderService.DTO.ApiResponse;
import com.project.OrderService.DTO.Product.ProductInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ApiResponse<ProductInfoResponse> getProduct(@PathVariable("id") UUID productId);

    @GetMapping("/api/products/ids")
    ApiResponse<List<ProductInfoResponse>> getProducts(@RequestParam("ids") List<UUID> productIds);

    @PostMapping("/api/inventory/{productId}/reserve")
    ApiResponse<Void> reserveStock(
            @PathVariable UUID productId,
            @RequestParam int quantity
    );

    @PostMapping("/api/inventory/{productId}/release")
    ApiResponse<Void> releaseStock(
            @PathVariable UUID productId,
            @RequestParam int quantity
    );
}