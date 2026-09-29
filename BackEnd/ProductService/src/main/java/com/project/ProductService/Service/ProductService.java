package com.project.ProductService.Service;

import com.project.ProductService.DTO.Product.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    Page<ProductResponse> getAllProducts(UUID categoryId, Pageable pageable);
    Page<ProductResponse> searchProducts(ProductSearchRequest request, Pageable pageable);

    ProductResponse getProduct(UUID id);

    ProductResponse updateProduct(
            UUID id,
            ProductUpdateRequest request
    );

    void deleteProduct(UUID id);

    List<ProductInfoResponse> getProductsByIds(List<UUID> ids);
}