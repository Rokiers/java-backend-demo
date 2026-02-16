package com.example.javabackenddemo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

public record UpdateCurrencyRatesRequest(
        @NotBlank String baseCurrency,
        @NotEmpty List<RateItem> rates
) {
    public record RateItem(@NotBlank String currency, BigDecimal rate) {}
}
