package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javabackenddemo.dto.request.UpdateCurrencyRatesRequest;
import com.example.javabackenddemo.dto.response.CurrencyRateResponse;
import com.example.javabackenddemo.entity.CurrencyRate;
import com.example.javabackenddemo.exception.UnsupportedCurrencyException;
import com.example.javabackenddemo.mapper.CurrencyRateMapper;
import com.example.javabackenddemo.service.CurrencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRateMapper currencyRateMapper;

    public CurrencyServiceImpl(CurrencyRateMapper currencyRateMapper) {
        this.currencyRateMapper = currencyRateMapper;
    }

    @Override
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        CurrencyRate rate = currencyRateMapper.selectOne(new LambdaQueryWrapper<CurrencyRate>()
                .eq(CurrencyRate::getBaseCurrency, fromCurrency)
                .eq(CurrencyRate::getTargetCurrency, toCurrency));
        if (rate == null) throw new UnsupportedCurrencyException("Unsupported currency: " + toCurrency);
        return amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<CurrencyRateResponse> getAllRates() {
        return currencyRateMapper.selectList(null).stream()
                .map(r -> new CurrencyRateResponse(r.getBaseCurrency(), r.getTargetCurrency(), r.getRate()))
                .toList();
    }

    @Override
    public List<CurrencyRateResponse> getRatesByBase(String baseCurrency) {
        return currencyRateMapper.selectList(new LambdaQueryWrapper<CurrencyRate>().eq(CurrencyRate::getBaseCurrency, baseCurrency))
                .stream().map(r -> new CurrencyRateResponse(r.getBaseCurrency(), r.getTargetCurrency(), r.getRate()))
                .toList();
    }

    @Override
    @Transactional
    public void updateRates(UpdateCurrencyRatesRequest request) {
        for (UpdateCurrencyRatesRequest.RateItem item : request.rates()) {
            CurrencyRate rate = currencyRateMapper.selectOne(new LambdaQueryWrapper<CurrencyRate>()
                    .eq(CurrencyRate::getBaseCurrency, request.baseCurrency())
                    .eq(CurrencyRate::getTargetCurrency, item.currency()));
            if (rate == null) {
                rate = CurrencyRate.builder()
                        .baseCurrency(request.baseCurrency())
                        .targetCurrency(item.currency())
                        .build();
            }
            rate.setRate(item.rate());
            if (rate.getId() == null) {
                currencyRateMapper.insert(rate);
            } else {
                currencyRateMapper.updateById(rate);
            }
        }
    }
}
