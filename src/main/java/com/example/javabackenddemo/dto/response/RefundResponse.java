package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundResponse(
        Long id, String refundNo, Long orderId, BigDecimal refundAmount,
        String reason, String description, String status,
        String adminRemark, LocalDateTime createdAt
) {}
