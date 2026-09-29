package com.project.OrderService.DTO.Product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductInfoResponse {

    private UUID id;
    private UUID sellerId;
    private String name;
    private String sku;
    private BigDecimal price;
    private String status;
}