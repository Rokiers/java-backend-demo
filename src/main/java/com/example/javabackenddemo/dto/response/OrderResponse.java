package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, String orderNo, BigDecimal totalAmount, String currency,
                             String status, String shippingAddress, String trackingNumber,
                             List<OrderItemResponse> items, LocalDateTime createdAt) {}
