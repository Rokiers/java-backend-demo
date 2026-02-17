package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HandleRefundRequest(
        @NotBlank String action,
        String adminRemark
) {}
