package com.example.javabackenddemo.dto.response;

import java.math.BigDecimal;

public record CurrencyConvertResponse(BigDecimal originalAmount, String fromCurrency,
                                       BigDecimal convertedAmount, String toCurrency, BigDecimal rate) {}
