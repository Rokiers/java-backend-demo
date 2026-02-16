package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.CreateCategoryRequest;
import com.example.javabackenddemo.dto.request.UpdateCategoryRequest;
import com.example.javabackenddemo.dto.response.CategoryTreeResponse;
import com.example.javabackenddemo.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryTreeResponse> getCategoryTree();
    Category getCategoryById(Long id);
    Category createCategory(CreateCategoryRequest request);
    Category updateCategory(Long id, UpdateCategoryRequest request);
    void deleteCategory(Long id);
    List<Long> getAllChildCategoryIds(Long categoryId);
}
