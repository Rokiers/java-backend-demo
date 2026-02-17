package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest(
        @NotBlank String name,
        @NotBlank String code,
        @NotBlank String couponType,
        @NotNull BigDecimal discountValue,
        BigDecimal minOrderAmount,
        @NotNull Integer totalCount,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime
) {}
