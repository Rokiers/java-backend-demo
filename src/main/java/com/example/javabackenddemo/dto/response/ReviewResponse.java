package com.example.javabackenddemo.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id, Long productId, Long userId, Integer rating,
        String content, String images, String adminReply,
        Boolean visible, LocalDateTime createdAt
) {}
