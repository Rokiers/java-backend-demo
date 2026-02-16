package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;

public record ProductListItemResponse(Long id, String name, String mainImage, BigDecimal price, String currency) {}
