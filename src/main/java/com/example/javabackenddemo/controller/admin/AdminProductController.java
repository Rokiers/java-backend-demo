package com.example.javabackenddemo.controller.admin;

import com.example.javabackenddemo.dto.request.*;
import com.example.javabackenddemo.dto.response.*;
import com.example.javabackenddemo.entity.Product;
import com.example.javabackenddemo.enums.ProductStatus;
import com.example.javabackenddemo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ApiResponse<Product> create(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return ApiResponse.success(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductListItemResponse>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name, @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ProductStatus status) {
        return ApiResponse.success(PageResponse.from(productService.adminList(name, categoryId, status, PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductDetail(id, null, null));
    }

    @PostMapping("/{id}/skus")
    public ApiResponse<Void> addSku(@PathVariable Long id, @Valid @RequestBody CreateSkuRequest request) {
        productService.addSku(id, request);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/skus/{skuId}")
    public ApiResponse<Void> updateSku(@PathVariable Long id, @PathVariable Long skuId,
            @RequestBody UpdateSkuRequest request) {
        productService.updateSku(id, skuId, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}/skus/{skuId}")
    public ApiResponse<Void> deleteSku(@PathVariable Long id, @PathVariable Long skuId) {
        productService.deleteSku(id, skuId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/translations")
    public ApiResponse<Void> addTranslation(@PathVariable Long id, @Valid @RequestBody TranslationRequest request) {
        productService.addTranslation(id, request);
        return ApiResponse.success(null);
    }
}
