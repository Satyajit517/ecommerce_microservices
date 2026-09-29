package com.project.ProductService.DTO.Inventory;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class InventoryResponse {

    private UUID id;

    private UUID productId;

    private int availableQuantity;

    private int reservedQuantity;

    private Instant updatedAt;
}