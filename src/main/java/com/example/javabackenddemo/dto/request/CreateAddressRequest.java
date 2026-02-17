package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(
        @NotBlank String receiverName,
        @NotBlank String receiverPhone,
        @NotBlank String province,
        @NotBlank String city,
        @NotBlank String district,
        @NotBlank String detailAddress,
        Boolean isDefault
) {}
