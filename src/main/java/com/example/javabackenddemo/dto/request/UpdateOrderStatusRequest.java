package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusRequest(@NotBlank String status, String trackingNumber) {}
