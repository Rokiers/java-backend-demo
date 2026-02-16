package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(@NotBlank String shippingAddress, @NotEmpty List<Long> cartItemIds) {}
