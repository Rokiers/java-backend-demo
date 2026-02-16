package com.example.javabackenddemo.exception;

import java.util.List;

public class InsufficientStockException extends RuntimeException {
    private final List<Long> skuIds;

    public InsufficientStockException(String message, List<Long> skuIds) {
        super(message);
        this.skuIds = skuIds;
    }

    public List<Long> getSkuIds() {
        return skuIds;
    }
}
