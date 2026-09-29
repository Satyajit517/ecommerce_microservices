package com.project.ProductService.DTO.Product;

import com.project.ProductService.DTO.Category.CategorySummaryResponse;
import com.project.ProductService.Enum.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductSummaryResponse {

    private UUID id;

    private String name;

    private String brand;

    private BigDecimal price;

    private String sku;

    private ProductStatus status;

    private CategorySummaryResponse category;
}