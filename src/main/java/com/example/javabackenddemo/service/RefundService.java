package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.CreateRefundRequest;
import com.example.javabackenddemo.dto.request.HandleRefundRequest;
import com.example.javabackenddemo.dto.response.RefundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RefundService {
    RefundResponse createRefund(Long userId, CreateRefundRequest request);
    Page<RefundResponse> listUserRefunds(Long userId, Pageable pageable);
    RefundResponse getRefundDetail(Long refundId);
    Page<RefundResponse> adminListRefunds(String status, Pageable pageable);
    RefundResponse handleRefund(Long refundId, HandleRefundRequest request);
}
