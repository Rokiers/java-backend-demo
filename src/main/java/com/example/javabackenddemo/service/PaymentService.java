package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.CreatePaymentRequest;
import com.example.javabackenddemo.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(Long userId, CreatePaymentRequest request);
    PaymentResponse mockPayCallback(String paymentNo);
    PaymentResponse getPaymentByOrderId(Long orderId);
}
