package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateProductRequest(
        @NotBlank String name,
        String description,
        String mainImage,
        @NotNull Long categoryId,
        String baseCurrency,
        List<AttributeRequest> attributes
) {}
