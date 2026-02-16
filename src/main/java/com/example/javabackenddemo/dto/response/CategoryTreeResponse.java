package com.example.javabackenddemo.dto.response;

import java.util.List;

public record CategoryTreeResponse(Long id, String name, Integer sort, List<CategoryTreeResponse> children) {}
