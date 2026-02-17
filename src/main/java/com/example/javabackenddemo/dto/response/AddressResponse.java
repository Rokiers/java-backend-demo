package com.example.javabackenddemo.dto.response;

import java.time.LocalDateTime;

public record AddressResponse(
        Long id, String receiverName, String receiverPhone,
        String province, String city, String district, String detailAddress,
        Boolean isDefault, LocalDateTime createdAt
) {}
