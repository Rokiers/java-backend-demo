package com.example.javabackenddemo.controller.admin;

import com.example.javabackenddemo.dto.request.CreateCategoryRequest;
import com.example.javabackenddemo.dto.request.UpdateCategoryRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CategoryTreeResponse;
import com.example.javabackenddemo.entity.Category;
import com.example.javabackenddemo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ApiResponse<Category> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ApiResponse.success(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(@PathVariable Long id, @RequestBody UpdateCategoryRequest request) {
        return ApiResponse.success(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<CategoryTreeResponse>> list() {
        return ApiResponse.success(categoryService.getCategoryTree());
    }
}
