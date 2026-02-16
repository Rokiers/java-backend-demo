package com.example.javabackenddemo.controller.admin;

import com.example.javabackenddemo.dto.request.UpdateCurrencyRatesRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CurrencyRateResponse;
import com.example.javabackenddemo.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/currencies")
public class AdminCurrencyController {

    private final CurrencyService currencyService;

    public AdminCurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/rates")
    public ApiResponse<List<CurrencyRateResponse>> rates() {
        return ApiResponse.success(currencyService.getAllRates());
    }

    @PutMapping("/rates")
    public ApiResponse<Void> updateRates(@Valid @RequestBody UpdateCurrencyRatesRequest request) {
        currencyService.updateRates(request);
        return ApiResponse.success(null);
    }
}
