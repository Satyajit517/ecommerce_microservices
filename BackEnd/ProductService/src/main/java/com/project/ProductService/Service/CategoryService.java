package com.project.ProductService.Service;

import com.project.ProductService.DTO.Category.CategoryCreateRequest;
import com.project.ProductService.DTO.Category.CategoryResponse;
import com.project.ProductService.DTO.Category.CategoryUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse createCategory(
            CategoryCreateRequest request
    );

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategory(UUID id);

    CategoryResponse updateCategory(
            UUID id,
            CategoryUpdateRequest request
    );

    void deleteCategory(UUID id);
}
