package com.project.ProductService.Service.Impl;

import com.project.ProductService.DTO.Product.*;
import com.project.ProductService.Entity.Category;
import com.project.ProductService.Entity.Product;
import com.project.ProductService.Enum.ProductStatus;
import com.project.ProductService.Exception.ConflictException;
import com.project.ProductService.Exception.ForbiddenException;
import com.project.ProductService.Exception.ResourceNotFoundException;
import com.project.ProductService.Mapper.ProductMapper;
import com.project.ProductService.Repository.CategoryRepository;
import com.project.ProductService.Repository.ProductRepository;
import com.project.ProductService.Service.CurrentUserService;
import com.project.ProductService.Service.ProductService;
import com.project.ProductService.Specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CurrentUserService currentUserService;

    private final CategoryRepository categoryRepository;

    private final ProductMapper productMapper;

    @PreAuthorize("hasRole('SELLER')")
    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {

        UUID sellerId = currentUserService.getCurrentUserId();

        if(productRepository.existsBySkuIgnoreCase(request.getSku()))
        {
            throw new ConflictException("SKU already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

        Product product = productMapper.toEntity(request);

        product.setSellerId(sellerId);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        Instant now = Instant.now();

        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(UUID categoryId, Pageable pageable) {

        Page<Product> products;

        if (categoryId == null) {

            products = productRepository.findAll(pageable);

        } else {

            products = productRepository.findByCategoryId(
                    categoryId,
                    pageable
            );
        }

        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(ProductSearchRequest request, Pageable pageable) {

        Specification<Product> specification =
                Specification
                        .where(ProductSpecification.search(request.getSearch()))

                        .and(ProductSpecification.hasCategory(request.getCategoryId()))

                        .and(ProductSpecification.priceGreaterThanOrEqualTo(request.getMinPrice()))

                        .and(ProductSpecification.priceLessThanOrEqualTo(request.getMaxPrice()))

                        .and(ProductSpecification.hasBrand(request.getBrand()))

                        .and(ProductSpecification.hasStatus(request.getStatus()));

        return productRepository
                .findAll(specification, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        return productMapper.toResponse(product);
    }

    @PreAuthorize("hasRole('SELLER')")
    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {

        UUID sellerId = currentUserService.getCurrentUserId();

        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        if(!product.getSellerId().equals(sellerId))
        {
            throw new ForbiddenException("You are not allowed to modify this product");
        }

        Category category = null;

        if(request.getCategoryId() != null)
        {
            category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

            product.setCategory(category);
        }

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUpdatedAt(Instant.now());

        return productMapper.toResponse(
                productRepository.save(product)
        );
    }

    @PreAuthorize("hasRole('SELLER')")
    @Override
    @Transactional
    public void deleteProduct(UUID id) {

        UUID sellerId = currentUserService.getCurrentUserId();

        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        if(!product.getSellerId().equals(sellerId))
        {
            throw new ForbiddenException("You are now allowed to delete this product");
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInfoResponse> getProductsByIds(List<UUID> ids) {

        List<Product> products = productRepository.findAllById(ids);

        return products.stream()
                .map(productMapper::toInfoResponse)
                .toList();
    }
}
