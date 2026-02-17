package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.CreatePaymentRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.PaymentResponse;
import com.example.javabackenddemo.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ApiResponse<PaymentResponse> create(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.success(paymentService.createPayment(userId, request));
    }

    @PostMapping("/callback/{paymentNo}")
    public ApiResponse<PaymentResponse> mockCallback(@PathVariable String paymentNo) {
        return ApiResponse.success(paymentService.mockPayCallback(paymentNo));
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<PaymentResponse> getByOrder(@PathVariable Long orderId) {
        return ApiResponse.success(paymentService.getPaymentByOrderId(orderId));
    }
}
