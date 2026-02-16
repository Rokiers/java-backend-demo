package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.CreateCategoryRequest;
import com.example.javabackenddemo.dto.request.UpdateCategoryRequest;
import com.example.javabackenddemo.dto.response.CategoryTreeResponse;
import com.example.javabackenddemo.entity.Category;
import com.example.javabackenddemo.exception.CategoryHasProductsException;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.CategoryRepository;
import com.example.javabackenddemo.repository.ProductRepository;
import com.example.javabackenddemo.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<CategoryTreeResponse> getCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIdIsNull();
        return roots.stream().map(this::buildTree).toList();
    }

    private CategoryTreeResponse buildTree(Category category) {
        List<Category> children = categoryRepository.findByParentId(category.getId());
        List<CategoryTreeResponse> childResponses = children.stream().map(this::buildTree).toList();
        return new CategoryTreeResponse(category.getId(), category.getName(), category.getSort(), childResponses);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @Override
    @Transactional
    public Category createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameAndParentId(request.name(), request.parentId())) {
            throw new DuplicateResourceException("Category name already exists at the same level");
        }
        Category category = Category.builder()
                .name(request.name())
                .parentId(request.parentId())
                .sort(request.sort() != null ? request.sort() : 0)
                .build();
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = getCategoryById(id);
        if (request.name() != null) category.setName(request.name());
        if (request.sort() != null) category.setSort(request.sort());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new CategoryHasProductsException("Category has products and cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    @Override
    public List<Long> getAllChildCategoryIds(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        collectChildIds(categoryId, ids);
        return ids;
    }

    private void collectChildIds(Long parentId, List<Long> ids) {
        List<Category> children = categoryRepository.findByParentId(parentId);
        for (Category child : children) {
            ids.add(child.getId());
            collectChildIds(child.getId(), ids);
        }
    }
}
