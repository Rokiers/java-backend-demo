package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.Payment;
import com.example.javabackenddemo.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByPaymentNo(String paymentNo);
    Optional<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
