package com.project.ProductService.DTO.Inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateInventoryRequest {

    @NotNull(message = "Product id is required")
    private UUID productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}