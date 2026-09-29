package com.project.ProductService.DTO.Product;

import com.project.ProductService.Enum.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductSearchRequest {

    private String search;

    private UUID categoryId;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String brand;

    private ProductStatus status;
}