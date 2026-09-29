package com.project.OrderService.Repository;

import com.project.OrderService.Entity.Order;
import com.project.OrderService.Enum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

    Page<Order> findByBuyerId(
            UUID buyerId,
            Pageable pageable
    );

    Page<Order> findByBuyerIdAndStatus(
            UUID buyerId,
            OrderStatus status,
            Pageable pageable
    );
}
