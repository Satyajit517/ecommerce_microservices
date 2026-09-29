package com.project.ProductService.Service;

import com.project.ProductService.DTO.Inventory.InventoryResponse;
import com.project.ProductService.DTO.Inventory.UpdateInventoryRequest;

import java.util.UUID;

public interface InventoryService {

    void reserveStock(UUID productId, int quantity);

    void releaseStock(UUID productId, int quantity);

    void deductStock(UUID productId, int quantity);

    InventoryResponse updateInventory(UpdateInventoryRequest request);

    InventoryResponse getInventory(UUID productId);

    InventoryResponse createInventory(UpdateInventoryRequest request);
}