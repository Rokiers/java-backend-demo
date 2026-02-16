package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.*;
import com.example.javabackenddemo.dto.response.ProductDetailResponse;
import com.example.javabackenddemo.dto.response.ProductListItemResponse;
import com.example.javabackenddemo.entity.Product;
import com.example.javabackenddemo.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductListItemResponse> listProducts(Pageable pageable, String currency, String lang);
    Page<ProductListItemResponse> listByCategory(Long categoryId, Pageable pageable, String currency, String lang);
    Page<ProductListItemResponse> search(String keyword, Pageable pageable, String currency, String lang);
    ProductDetailResponse getProductDetail(Long id, String currency, String lang);
    Product createProduct(CreateProductRequest request);
    Product updateProduct(Long id, UpdateProductRequest request);
    void deactivateProduct(Long id);
    Page<ProductListItemResponse> adminList(String name, Long categoryId, ProductStatus status, Pageable pageable);
    void addSku(Long productId, CreateSkuRequest request);
    void updateSku(Long productId, Long skuId, UpdateSkuRequest request);
    void deleteSku(Long productId, Long skuId);
    void addTranslation(Long productId, TranslationRequest request);
}
