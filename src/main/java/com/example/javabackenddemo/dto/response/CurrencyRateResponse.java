package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;

public record CurrencyRateResponse(String baseCurrency, String targetCurrency, BigDecimal rate) {}
