package com.project.OrderService.Controller;

import com.project.OrderService.DTO.ApiResponse;
import com.project.OrderService.DTO.Order.CreateOrderRequest;
import com.project.OrderService.DTO.Order.OrderResponse;
import com.project.OrderService.DTO.PageResponse;
import com.project.OrderService.Enum.OrderStatus;
import com.project.OrderService.Service.CurrentUserService;
import com.project.OrderService.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request)
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        OrderResponse response = orderService.createOrder(buyerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true,
                "Order created successfully",
                response,
                Instant.now()
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID orderId)
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        OrderResponse response = orderService.getOrder(buyerId, orderId);

        return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Order fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrders(@PageableDefault(size = 10) Pageable pageable)
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        Page<OrderResponse> page = orderService.getOrders(buyerId, pageable);

        PageResponse<OrderResponse> response = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Orders fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @PageableDefault(size = 10) Pageable pageable
    )
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        Page<OrderResponse> page = orderService.getOrdersByStatus(buyerId, status, pageable);

        PageResponse<OrderResponse> response = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Orders fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable UUID orderId)
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        orderService.cancelOrder(buyerId, orderId);

        return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Order cancelled successfully",
                        null,
                        Instant.now()
                )
        );
    }

}
