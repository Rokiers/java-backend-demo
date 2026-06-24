package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javabackenddemo.dto.request.CreateCategoryRequest;
import com.example.javabackenddemo.dto.request.UpdateCategoryRequest;
import com.example.javabackenddemo.dto.response.CategoryTreeResponse;
import com.example.javabackenddemo.entity.Category;
import com.example.javabackenddemo.exception.CategoryHasProductsException;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.CategoryMapper;
import com.example.javabackenddemo.mapper.ProductMapper;
import com.example.javabackenddemo.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<CategoryTreeResponse> getCategoryTree() {
        List<Category> roots = categoryMapper.selectList(new LambdaQueryWrapper<Category>().isNull(Category::getParentId));
        return roots.stream().map(this::buildTree).toList();
    }

    private CategoryTreeResponse buildTree(Category category) {
        List<Category> children = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, category.getId()));
        List<CategoryTreeResponse> childResponses = children.stream().map(this::buildTree).toList();
        return new CategoryTreeResponse(category.getId(), category.getName(), category.getSort(), childResponses);
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) throw new ResourceNotFoundException("Category not found: " + id);
        return category;
    }

    @Override
    @Transactional
    public Category createCategory(CreateCategoryRequest request) {
        if (categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, request.name())
                .eq(request.parentId() != null, Category::getParentId, request.parentId())) > 0) {
            throw new DuplicateResourceException("Category name already exists at the same level");
        }
        Category category = Category.builder()
                .name(request.name())
                .parentId(request.parentId())
                .sort(request.sort() != null ? request.sort() : 0)
                .build();
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = getCategoryById(id);
        if (request.name() != null) category.setName(request.name());
        if (request.sort() != null) category.setSort(request.sort());
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        if (productMapper.existsByCategoryId(id)) {
            throw new CategoryHasProductsException("Category has products and cannot be deleted");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public List<Long> getAllChildCategoryIds(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        collectChildIds(categoryId, ids);
        return ids;
    }

    private void collectChildIds(Long parentId, List<Long> ids) {
        List<Category> children = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, parentId));
        for (Category child : children) {
            ids.add(child.getId());
            collectChildIds(child.getId(), ids);
        }
    }
}
