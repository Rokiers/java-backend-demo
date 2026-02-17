package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id, String name, String code, String couponType,
        BigDecimal discountValue, BigDecimal minOrderAmount,
        Integer totalCount, Integer usedCount,
        LocalDateTime startTime, LocalDateTime endTime, Boolean enabled
) {}
