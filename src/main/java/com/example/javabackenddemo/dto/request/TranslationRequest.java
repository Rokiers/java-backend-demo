package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TranslationRequest(@NotBlank String languageCode, @NotBlank String name, String description) {}
