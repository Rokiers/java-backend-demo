package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record UpdateInventoryRequest(@NotNull @Min(0) Integer quantity, Integer alertThreshold) {}
