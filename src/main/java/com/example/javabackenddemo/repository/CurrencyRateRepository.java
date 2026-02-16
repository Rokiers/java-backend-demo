package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findByBaseCurrencyAndTargetCurrency(String baseCurrency, String targetCurrency);
    List<CurrencyRate> findByBaseCurrency(String baseCurrency);
}
