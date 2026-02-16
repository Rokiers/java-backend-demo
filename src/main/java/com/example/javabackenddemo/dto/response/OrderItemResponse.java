package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, Long skuId, String productNameSnapshot, String skuSpecSnapshot,
                                 BigDecimal unitPrice, Integer quantity, BigDecimal subtotal) {}
