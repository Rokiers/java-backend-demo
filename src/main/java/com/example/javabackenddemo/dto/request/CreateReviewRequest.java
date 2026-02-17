package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
        @NotNull Long orderId,
        @NotNull @Min(1) @Max(5) Integer rating,
        String content,
        String images
) {}
