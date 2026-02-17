package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateRefundRequest(
        @NotNull Long orderId,
        @NotNull BigDecimal refundAmount,
        @NotBlank String reason,
        String description
) {}
