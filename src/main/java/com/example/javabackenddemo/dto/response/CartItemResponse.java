package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(Long id, Long skuId, String productName, String mainImage,
                                String specifications, BigDecimal unitPrice, Integer quantity, BigDecimal subtotal) {}
