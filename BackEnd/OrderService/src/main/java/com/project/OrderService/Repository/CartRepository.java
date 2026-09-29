package com.project.OrderService.Repository;

import com.project.OrderService.Entity.Cart;
import com.project.OrderService.Enum.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByBuyerId(UUID buyerId);

    Optional<Cart> findByBuyerIdAndStatus(
            UUID buyerId,
            CartStatus status
    );

    boolean existsByBuyerId(UUID buyerId);
}
