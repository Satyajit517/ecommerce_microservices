package com.project.ProductService.DTO.Product;

import com.project.ProductService.DTO.Category.CategorySummaryResponse;
import com.project.ProductService.Enum.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse {

    private UUID id;

    private UUID sellerId;

    private CategorySummaryResponse category;

    private String name;

    private String brand;

    private String description;

    private BigDecimal price;

    private String sku;

    private ProductStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}