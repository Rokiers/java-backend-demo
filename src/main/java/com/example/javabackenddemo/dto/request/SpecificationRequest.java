package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SpecificationRequest(@NotBlank String specName, @NotBlank String specValue) {}
