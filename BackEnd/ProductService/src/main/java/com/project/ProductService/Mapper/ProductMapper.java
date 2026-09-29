package com.project.ProductService.Mapper;

import com.project.ProductService.DTO.Category.CategorySummaryResponse;
import com.project.ProductService.DTO.Product.ProductCreateRequest;
import com.project.ProductService.DTO.Product.ProductInfoResponse;
import com.project.ProductService.DTO.Product.ProductResponse;
import com.project.ProductService.DTO.Product.ProductSummaryResponse;
import com.project.ProductService.Entity.Category;
import com.project.ProductService.Entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductCreateRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());

        return product;
    }

    public ProductResponse toResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setSellerId(product.getSellerId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setSku(product.getSku());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            response.setCategory(toCategorySummary(product.getCategory()));
        }

        return response;
    }

    public ProductSummaryResponse toSummaryResponse(Product product) {

        ProductSummaryResponse response = new ProductSummaryResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setPrice(product.getPrice());
        response.setSku(product.getSku());
        response.setStatus(product.getStatus());

        if (product.getCategory() != null) {
            response.setCategory(toCategorySummary(product.getCategory()));
        }

        return response;
    }

    private CategorySummaryResponse toCategorySummary(Category category) {

        CategorySummaryResponse response =
                new CategorySummaryResponse();

        response.setId(category.getId());
        response.setName(category.getName());

        return response;
    }

    public ProductInfoResponse toInfoResponse(Product product) {

        ProductInfoResponse response = new ProductInfoResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setPrice(product.getPrice());
        response.setSellerId(product.getSellerId());
        response.setStatus(product.getStatus().name());

        return response;
    }
}