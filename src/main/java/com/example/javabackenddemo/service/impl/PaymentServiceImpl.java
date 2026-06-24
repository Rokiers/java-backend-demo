package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.example.javabackenddemo.mapper.OrderMapper;
import com.example.javabackenddemo.mapper.PaymentMapper;
import com.example.javabackenddemo.service.OrderService;
import com.example.javabackenddemo.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    public PaymentServiceImpl(PaymentMapper paymentMapper, OrderMapper orderMapper,
                              OrderService orderService) {
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(Long userId, CreatePaymentRequest request) {
        Order order = orderMapper.selectById(request.orderId());
        if (order == null) throw new ResourceNotFoundException("Order not found: " + request.orderId());
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidStateTransitionException("Order is not in PENDING status");
        }
        Payment existing = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, order.getId())
                .eq(Payment::getStatus, PaymentStatus.PENDING));
        if (existing != null) throw new InvalidStateTransitionException("Payment already exists for this order");

        String paymentNo = "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
        Payment payment = Payment.builder()
                .paymentNo(paymentNo)
                .orderId(order.getId())
                .userId(userId)
                .amount(order.getTotalAmount())
                .currency(order.getCurrency())
                .paymentMethod(PaymentMethod.valueOf(request.paymentMethod()))
                .build();
        paymentMapper.insert(payment);
        return toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse mockPayCallback(String paymentNo) {
        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>().eq(Payment::getPaymentNo, paymentNo));
        if (payment == null) throw new ResourceNotFoundException("Payment not found: " + paymentNo);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateTransitionException("Payment is not in PENDING status");
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentMapper.updateById(payment);
        orderService.updateOrderStatus(payment.getOrderId(),
                new UpdateOrderStatusRequest("PAID", null));
        return toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>().eq(Payment::getOrderId, orderId));
        if (payment == null) throw new ResourceNotFoundException("Payment not found for order: " + orderId);
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getPaymentNo(), p.getOrderId(), p.getAmount(),
                p.getCurrency(), p.getStatus().name(), p.getPaymentMethod().name(),
                p.getPaidAt(), p.getCreatedAt());
    }
}
