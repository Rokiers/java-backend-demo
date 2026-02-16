package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(@NotNull Long skuId, @NotNull @Positive Integer quantity) {}
