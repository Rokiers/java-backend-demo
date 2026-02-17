package com.example.javabackenddemo.dto.request;

public record UpdateAddressRequest(
        String receiverName,
        String receiverPhone,
        String province,
        String city,
        String district,
        String detailAddress,
        Boolean isDefault
) {}
