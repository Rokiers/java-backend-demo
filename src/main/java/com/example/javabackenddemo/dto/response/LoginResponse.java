package com.example.javabackenddemo.dto.response;

public record LoginResponse(
        String token,
        Long userId,
        String username,
        String role
) {}
