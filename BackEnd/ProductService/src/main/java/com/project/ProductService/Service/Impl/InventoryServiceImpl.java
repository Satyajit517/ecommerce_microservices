package com.project.ProductService.Service.Impl;

import com.project.ProductService.DTO.Inventory.InventoryResponse;
import com.project.ProductService.DTO.Inventory.UpdateInventoryRequest;
import com.project.ProductService.Entity.Inventory;
import com.project.ProductService.Entity.Product;
import com.project.ProductService.Exception.*;
import com.project.ProductService.Repository.InventoryRepository;
import com.project.ProductService.Repository.ProductRepository;
import com.project.ProductService.Service.CurrentUserService;
import com.project.ProductService.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public void reserveStock(UUID productId, int quantity) {

        validateQuantity(quantity);

        Inventory inventory = findInventory(productId);

        if(inventory.getAvailableQuantity() < quantity )
        {
            throw new InsufficientStockException("Insufficient Stock");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void releaseStock(UUID productId, int quantity) {

        validateQuantity(quantity);

        Inventory inventory = findInventory(productId);

        if(inventory.getReservedQuantity() < quantity)
        {
            throw new InvalidInventoryOperationException("Cannot release more stock than reserved");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void deductStock(UUID productId, int quantity) {

        validateQuantity(quantity);

        Inventory inventory = findInventory(productId);

        if(inventory.getReservedQuantity() < quantity)
        {
            throw new InvalidInventoryOperationException("Cannot deduct more stock than released");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        inventoryRepository.save(inventory);
    }

    @PreAuthorize("hasRole('SELLER')")
    @Override
    @Transactional
    public InventoryResponse updateInventory(UpdateInventoryRequest request) {

        UUID sellerId = currentUserService.getCurrentUserId();

        Inventory inventory = inventoryRepository
                .findByProductId(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product: " + request.getProductId()
                ));

        if(!inventory.getProduct().getSellerId().equals(sellerId))
        {
            throw new ForbiddenException("You are not allowed to modify this product's inventory");
        }

        inventory.setAvailableQuantity(request.getQuantity());
        inventory.setUpdatedAt(Instant.now());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        InventoryResponse response = new InventoryResponse();
        response.setId(updatedInventory.getId());
        response.setProductId(updatedInventory.getProduct().getId());
        response.setAvailableQuantity(updatedInventory.getAvailableQuantity());
        response.setReservedQuantity(updatedInventory.getReservedQuantity());
        response.setUpdatedAt(updatedInventory.getUpdatedAt());

        return response;
    }

    @PreAuthorize("hasRole('SELLER')")
    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(UUID productId) {

        UUID sellerId = currentUserService.getCurrentUserId();

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product: " + productId
                ));

        if(!inventory.getProduct().getSellerId().equals(sellerId))
        {
            throw new ForbiddenException("You are not allowed to see this product");
        }

        InventoryResponse response = new InventoryResponse();

        response.setId(inventory.getId());
        response.setProductId(inventory.getProduct().getId());
        response.setAvailableQuantity(inventory.getAvailableQuantity());
        response.setReservedQuantity(inventory.getReservedQuantity());
        response.setUpdatedAt(inventory.getUpdatedAt());

        return response;
    }

    private Inventory findInventory(UUID productId)
    {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow( () ->
                        new ResourceNotFoundException("Inventory not found for product: " + productId));
    }

    private void validateQuantity(int quantity)
    {
        if(quantity <= 0)
        {
            throw new IllegalArgumentException("Quantity must be greater than Zero");
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @Override
    @Transactional
    public InventoryResponse createInventory(UpdateInventoryRequest request) {

        UUID sellerId = currentUserService.getCurrentUserId();

        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new ConflictException("Inventory already exists for this product");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + request.getProductId()
                ));

        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenException(
                    "You are not allowed to create inventory for this product"
            );
        }

        Inventory inventory = new Inventory();

        inventory.setProduct(product);
        inventory.setAvailableQuantity(request.getQuantity());
        inventory.setReservedQuantity(0);
        inventory.setUpdatedAt(Instant.now());

        Inventory savedInventory = inventoryRepository.save(inventory);

        InventoryResponse response = new InventoryResponse();

        response.setId(savedInventory.getId());
        response.setProductId(savedInventory.getProduct().getId());
        response.setAvailableQuantity(savedInventory.getAvailableQuantity());
        response.setReservedQuantity(savedInventory.getReservedQuantity());
        response.setUpdatedAt(savedInventory.getUpdatedAt());

        return response;
    }
}
