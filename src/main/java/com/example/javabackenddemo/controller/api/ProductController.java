package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.dto.response.ProductDetailResponse;
import com.example.javabackenddemo.dto.response.ProductListItemResponse;
import com.example.javabackenddemo.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductListItemResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String lang) {
        return ApiResponse.success(PageResponse.from(productService.listProducts(PageRequest.of(page, size), currency, lang)));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> detail(@PathVariable Long id,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String lang) {
        return ApiResponse.success(productService.getProductDetail(id, currency, lang));
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<PageResponse<ProductListItemResponse>> byCategory(@PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String currency, @RequestParam(required = false) String lang) {
        return ApiResponse.success(PageResponse.from(productService.listByCategory(categoryId, PageRequest.of(page, size), currency, lang)));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<ProductListItemResponse>> search(@RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String currency, @RequestParam(required = false) String lang) {
        return ApiResponse.success(PageResponse.from(productService.search(keyword, PageRequest.of(page, size), currency, lang)));
    }
}
