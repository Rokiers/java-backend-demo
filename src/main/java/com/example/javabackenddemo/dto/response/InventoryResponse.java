package com.example.javabackenddemo.dto.response;

public record InventoryResponse(Long skuId, String skuCode, Integer quantity, Integer alertThreshold, boolean lowStock) {}
