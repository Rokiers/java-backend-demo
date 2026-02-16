package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.UpdateCurrencyRatesRequest;
import com.example.javabackenddemo.dto.response.CurrencyRateResponse;
import com.example.javabackenddemo.entity.CurrencyRate;
import com.example.javabackenddemo.exception.UnsupportedCurrencyException;
import com.example.javabackenddemo.repository.CurrencyRateRepository;
import com.example.javabackenddemo.service.CurrencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRateRepository currencyRateRepository;

    public CurrencyServiceImpl(CurrencyRateRepository currencyRateRepository) {
        this.currencyRateRepository = currencyRateRepository;
    }

    @Override
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        CurrencyRate rate = currencyRateRepository.findByBaseCurrencyAndTargetCurrency(fromCurrency, toCurrency)
                .orElseThrow(() -> new UnsupportedCurrencyException("Unsupported currency: " + toCurrency));
        return amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<CurrencyRateResponse> getAllRates() {
        return currencyRateRepository.findAll().stream()
                .map(r -> new CurrencyRateResponse(r.getBaseCurrency(), r.getTargetCurrency(), r.getRate()))
                .toList();
    }

    @Override
    public List<CurrencyRateResponse> getRatesByBase(String baseCurrency) {
        return currencyRateRepository.findByBaseCurrency(baseCurrency).stream()
                .map(r -> new CurrencyRateResponse(r.getBaseCurrency(), r.getTargetCurrency(), r.getRate()))
                .toList();
    }

    @Override
    @Transactional
    public void updateRates(UpdateCurrencyRatesRequest request) {
        for (UpdateCurrencyRatesRequest.RateItem item : request.rates()) {
            CurrencyRate rate = currencyRateRepository
                    .findByBaseCurrencyAndTargetCurrency(request.baseCurrency(), item.currency())
                    .orElse(CurrencyRate.builder()
                            .baseCurrency(request.baseCurrency())
                            .targetCurrency(item.currency())
                            .build());
            rate.setRate(item.rate());
            currencyRateRepository.save(rate);
        }
    }
}
