package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserCouponResponse(
        Long id, Long couponId, String couponName, String couponType,
        BigDecimal discountValue, BigDecimal minOrderAmount,
        Boolean used, LocalDateTime claimedAt, LocalDateTime endTime
) {}
