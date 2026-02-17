package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id, String paymentNo, Long orderId, BigDecimal amount,
        String currency, String status, String paymentMethod,
        LocalDateTime paidAt, LocalDateTime createdAt
) {}
