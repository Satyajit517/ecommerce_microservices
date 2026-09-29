package com.project.ProductService.Mapper;

import com.project.ProductService.DTO.Inventory.InventoryResponse;
import com.project.ProductService.Entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {

        InventoryResponse response = new InventoryResponse();

        response.setId(inventory.getId());
        response.setProductId(inventory.getProduct().getId());
        response.setAvailableQuantity(
                inventory.getAvailableQuantity()
        );
        response.setReservedQuantity(
                inventory.getReservedQuantity()
        );
        response.setUpdatedAt(inventory.getUpdatedAt());

        return response;
    }
}