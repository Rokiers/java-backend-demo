package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AttributeRequest(@NotBlank String attrName, @NotBlank String attrValue) {}
