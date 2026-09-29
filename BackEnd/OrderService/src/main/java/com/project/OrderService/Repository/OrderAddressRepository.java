package com.project.OrderService.Repository;

import com.project.OrderService.Entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, UUID> {

    Optional<OrderAddress> findByOrderId(UUID orderId);
}
