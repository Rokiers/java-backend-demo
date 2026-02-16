package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(@NotBlank String name, Long parentId, Integer sort) {}
