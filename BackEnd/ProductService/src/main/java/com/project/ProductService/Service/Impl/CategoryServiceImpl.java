package com.project.ProductService.Service.Impl;

import com.project.ProductService.DTO.Category.CategoryCreateRequest;
import com.project.ProductService.DTO.Category.CategoryResponse;
import com.project.ProductService.DTO.Category.CategoryUpdateRequest;
import com.project.ProductService.Entity.Category;
import com.project.ProductService.Exception.ConflictException;
import com.project.ProductService.Exception.ResourceNotFoundException;
import com.project.ProductService.Mapper.CategoryMapper;
import com.project.ProductService.Repository.CategoryRepository;
import com.project.ProductService.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        if(categoryRepository.existsByNameIgnoreCase(request.getName()))
        {
            throw new ConflictException("Category already exists");
        }

        Category category = categoryMapper.toEntity(request);

        Instant now = Instant.now();

        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category does not exist"));

        return categoryMapper.toResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category does not exist"));

        if (!category.getName().equalsIgnoreCase(request.getName())
                &&
            categoryRepository.existsByNameIgnoreCase(request.getName())) {

                throw new ConflictException(
                    "Category name already exists"
            );
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setUpdatedAt(Instant.now());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteCategory(UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category does not exist"));

        categoryRepository.delete(category);
    }
}
