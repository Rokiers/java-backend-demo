package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SkuResponse(Long id, String skuCode, BigDecimal price,
                           List<SpecificationResponse> specifications, Integer stockQuantity, boolean inStock) {}
