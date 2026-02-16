package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.UpdateCurrencyRatesRequest;
import com.example.javabackenddemo.dto.response.CurrencyRateResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyService {
    BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency);
    List<CurrencyRateResponse> getAllRates();
    List<CurrencyRateResponse> getRatesByBase(String baseCurrency);
    void updateRates(UpdateCurrencyRatesRequest request);
}
