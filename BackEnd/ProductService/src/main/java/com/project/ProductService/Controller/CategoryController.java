package com.project.ProductService.Controller;

import com.project.ProductService.DTO.ApiResponse;
import com.project.ProductService.DTO.Category.CategoryCreateRequest;
import com.project.ProductService.DTO.Category.CategoryResponse;
import com.project.ProductService.DTO.Category.CategoryUpdateRequest;
import com.project.ProductService.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request
    )
    {
        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Category created successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories()
    {
        List<CategoryResponse> responses = categoryService.getAllCategories();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Categories fetched successfully",
                        responses,
                        Instant.now()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable UUID id)
    {

        CategoryResponse response = categoryService.getCategory(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Category fetched successfully",
                response,
                Instant.now()
              )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable UUID id,
                                                                        @RequestBody CategoryUpdateRequest request)
    {
        CategoryResponse response = categoryService.updateCategory(id, request);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Category updated successfully",
                response,
                Instant.now()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id)
    {
        categoryService.deleteCategory(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Category deleted successfully",
                null,
                Instant.now()
                )
        );
    }
}
