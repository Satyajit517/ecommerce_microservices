package com.project.ProductService.Repository;

import com.project.ProductService.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>,
                                           JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByCategoryId(UUID categoryId);

    Page<Product> findByCategoryId(
            UUID categoryId,
            Pageable pageable
    );

}
