package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CategoryTreeResponse;
import com.example.javabackenddemo.entity.Category;
import com.example.javabackenddemo.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryTreeResponse>> tree() {
        return ApiResponse.success(categoryService.getCategoryTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> detail(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getCategoryById(id));
    }
}
