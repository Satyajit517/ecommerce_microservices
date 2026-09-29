package com.project.ProductService.Controller;

import com.project.ProductService.DTO.ApiResponse;
import com.project.ProductService.DTO.PageResponse;
import com.project.ProductService.DTO.Product.*;
import com.project.ProductService.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request)
    {
        ProductResponse product = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Product created Successfully",
                        product,
                        Instant.now()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable UUID id)
    {
        ProductResponse response = productService.getProduct(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Product fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) UUID categoryId,
            @PageableDefault(size = 10) Pageable pageable
            )
    {
        Page<ProductResponse> page = productService.getAllProducts(categoryId, pageable);

        PageResponse<ProductResponse> pageResponse = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Products fetched Successfully",
                        pageResponse,
                        Instant.now()
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @ModelAttribute ProductSearchRequest request,
            @PageableDefault(size = 10) Pageable pageable
    )
    {
        Page<ProductResponse> page = productService.searchProducts(request, pageable);

        PageResponse<ProductResponse> pageResponse = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Products searched successfully",
                        pageResponse,
                        Instant.now()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateRequest request
    )
    {
        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Product updated successfully",
                response,
                Instant.now()
                )
        );
    }

    @DeleteMapping("/id")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProduct(@PathVariable UUID id)
    {
        productService.deleteProduct(id);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Product deleted successfully",
                null,
                Instant.now()
        ));
    }

    @GetMapping("/ids")
    public ApiResponse<List<ProductInfoResponse>> getProductsByIds(@RequestParam List<UUID> ids) {

        List<ProductInfoResponse> products = productService.getProductsByIds(ids);

        return new ApiResponse<>(
                true,
                "Products fetched successfully",
                products,
                Instant.now()
        );
    }
}
