package com.example.javabackenddemo.dto.response;

import java.util.List;

public record ProductDetailResponse(
        Long id, String name, String description, String mainImage,
        Long categoryId, String categoryName,
        List<AttributeResponse> attributes, List<SkuResponse> skus, String currency
) {}
