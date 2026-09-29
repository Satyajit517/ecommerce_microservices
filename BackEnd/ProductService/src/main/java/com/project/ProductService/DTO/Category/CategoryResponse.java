package com.project.ProductService.DTO.Category;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class CategoryResponse {

    private UUID id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;
}