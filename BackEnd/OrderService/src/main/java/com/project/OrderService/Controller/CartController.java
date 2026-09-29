package com.project.OrderService.Controller;

import com.project.OrderService.DTO.ApiResponse;
import com.project.OrderService.DTO.Cart.AddToCartRequest;
import com.project.OrderService.DTO.Cart.CartResponse;
import com.project.OrderService.DTO.Cart.UpdateCartItemRequest;
import com.project.OrderService.Service.CartService;
import com.project.OrderService.Service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart()
    {
        UUID buyerId = currentUserService.getCurrentUserId();
        CartResponse response = cartService.getCart(buyerId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Cart fetched successfully",
                response,
                Instant.now()
                )
        );
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody AddToCartRequest request)
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        CartResponse response = cartService.addItem(
                buyerId, request.getProductId(), request.getQuantity());

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Successfully added to cart",
                response,
                Instant.now()
            )
        );
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCart(@PathVariable UUID productId,
                                                                @Valid @RequestBody UpdateCartItemRequest request)
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        CartResponse response = cartService.updateItem(buyerId, productId, request.getQuantity());

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Cart Item updated successfully",
                response,
                Instant.now()
                )
        );
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(@PathVariable UUID productId)
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        CartResponse response = cartService.removeItem(buyerId, productId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Cart item deleted successfully",
                response,
                Instant.now()
            )
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart()
    {
        UUID buyerId = currentUserService.getCurrentUserId();

        cartService.clearCart(buyerId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Cart cleared successfully",
                null,
                Instant.now()
            )
        );
    }
}
