package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Long id, List<CartItemResponse> items, BigDecimal totalAmount, String currency) {}
