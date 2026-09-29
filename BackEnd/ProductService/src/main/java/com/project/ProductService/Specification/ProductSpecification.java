package com.project.ProductService.Specification;

import com.project.ProductService.Entity.Product;
import com.project.ProductService.Enum.ProductStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> search(String search) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return null;
            }

            String pattern =
                    "%" + search.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("name")
                            ),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("brand")
                            ),
                            pattern
                    )
            );
        };
    }

    public static Specification<Product> hasCategory(UUID categoryId) {

        return (root, query, criteriaBuilder) -> {

            if (categoryId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("category").get("id"),
                    categoryId
            );
        };
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(BigDecimal minPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }

    public static Specification<Product> priceLessThanOrEqualTo(BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }

    public static Specification<Product> hasBrand(String brand) {

        return (root, query, criteriaBuilder) -> {

            if (brand == null || brand.isBlank()) {
                return null;
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(
                            root.get("brand")
                    ),
                    brand.toLowerCase()
            );
        };
    }

    public static Specification<Product> hasStatus(ProductStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }
}