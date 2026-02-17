package com.example.javabackenddemo.repository;

import com.example.javabackenddemo.entity.RefundRequest;
import com.example.javabackenddemo.enums.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    Page<RefundRequest> findByUserId(Long userId, Pageable pageable);
    Optional<RefundRequest> findByOrderIdAndUserId(Long orderId, Long userId);
    Page<RefundRequest> findByStatus(RefundStatus status, Pageable pageable);
}
