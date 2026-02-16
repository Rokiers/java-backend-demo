package com.example.javabackenddemo.dto.request;

import java.util.List;

public record UpdateProductRequest(
        String name,
        String description,
        String mainImage,
        Long categoryId,
        List<AttributeRequest> attributes
) {}
