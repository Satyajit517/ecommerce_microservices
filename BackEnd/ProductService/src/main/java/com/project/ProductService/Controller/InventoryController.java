package com.project.ProductService.Controller;


import com.project.ProductService.DTO.ApiResponse;
import com.project.ProductService.DTO.Inventory.InventoryResponse;
import com.project.ProductService.DTO.Inventory.UpdateInventoryRequest;
import com.project.ProductService.Service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(
            @Valid @RequestBody UpdateInventoryRequest request) {

        InventoryResponse response = inventoryService.createInventory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Inventory created successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(@Valid
                                                                          @RequestBody
                                                                          UpdateInventoryRequest request)
    {
        InventoryResponse response = inventoryService.updateInventory(request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Inventory updated successfully",
                response,
                Instant.now()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(@PathVariable UUID id)
    {
        InventoryResponse response = inventoryService.getInventory(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                    true,
                    "Inventory fetched successfully",
                    response,
                    Instant.now()
                )
        );
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<ApiResponse<Void>> reserveStock(
            @PathVariable UUID productId,
            @RequestParam int quantity) {

        inventoryService.reserveStock(productId, quantity);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Stock reserved successfully",
                        null,
                        Instant.now()
                )
        );
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<ApiResponse<Void>> releaseStock(
            @PathVariable UUID productId,
            @RequestParam int quantity) {

        inventoryService.releaseStock(productId, quantity);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Stock released successfully",
                        null,
                        Instant.now()
                )
        );
    }
}
