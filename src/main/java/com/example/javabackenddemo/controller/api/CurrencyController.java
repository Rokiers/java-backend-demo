package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CurrencyConvertResponse;
import com.example.javabackenddemo.dto.response.CurrencyRateResponse;
import com.example.javabackenddemo.service.CurrencyService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public ApiResponse<List<CurrencyRateResponse>> list() {
        return ApiResponse.success(currencyService.getAllRates());
    }

    @GetMapping("/convert")
    public ApiResponse<CurrencyConvertResponse> convert(@RequestParam BigDecimal amount,
            @RequestParam String from, @RequestParam String to) {
        BigDecimal converted = currencyService.convert(amount, from, to);
        BigDecimal rate = converted.divide(amount, 6, java.math.RoundingMode.HALF_UP);
        return ApiResponse.success(new CurrencyConvertResponse(amount, from, converted, to, rate));
    }
}
