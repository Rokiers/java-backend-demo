package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.CreatePaymentRequest;
import com.example.javabackenddemo.dto.request.UpdateOrderStatusRequest;
import com.example.javabackenddemo.dto.response.PaymentResponse;
import com.example.javabackenddemo.entity.Order;
import com.example.javabackenddemo.entity.Payment;
import com.example.javabackenddemo.enums.OrderStatus;
import com.example.javabackenddemo.enums.PaymentMethod;
import com.example.javabackenddemo.enums.PaymentStatus;
import com.example.javabackenddemo.exception.InvalidStateTransitionException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.OrderRepository;
import com.example.javabackenddemo.repository.PaymentRepository;
import com.example.javabackenddemo.service.OrderService;
import com.example.javabackenddemo.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository,
                              OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(Long userId, CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidStateTransitionException("Order is not in PENDING status");
        }
        // Check if payment already exists
        paymentRepository.findByOrderIdAndStatus(order.getId(), PaymentStatus.PENDING)
                .ifPresent(p -> { throw new InvalidStateTransitionException("Payment already exists for this order"); });

        String paymentNo = "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
        Payment payment = Payment.builder()
                .paymentNo(paymentNo)
                .orderId(order.getId())
                .userId(userId)
                .amount(order.getTotalAmount())
                .currency(order.getCurrency())
                .paymentMethod(PaymentMethod.valueOf(request.paymentMethod()))
                .build();
        return toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentResponse mockPayCallback(String paymentNo) {
        Payment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentNo));
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateTransitionException("Payment is not in PENDING status");
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        // Update order status to PAID
        orderService.updateOrderStatus(payment.getOrderId(),
                new UpdateOrderStatusRequest("PAID", null));
        return toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getPaymentNo(), p.getOrderId(), p.getAmount(),
                p.getCurrency(), p.getStatus().name(), p.getPaymentMethod().name(),
                p.getPaidAt(), p.getCreatedAt());
    }
}
