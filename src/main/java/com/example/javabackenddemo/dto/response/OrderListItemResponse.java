package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderListItemResponse(Long id, String orderNo, String status, BigDecimal totalAmount,
                                     String currency, LocalDateTime createdAt) {}
