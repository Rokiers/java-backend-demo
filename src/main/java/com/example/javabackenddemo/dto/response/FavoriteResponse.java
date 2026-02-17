package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FavoriteResponse(
        Long id, Long productId, String productName,
        String mainImage, BigDecimal price, LocalDateTime createdAt
) {}
