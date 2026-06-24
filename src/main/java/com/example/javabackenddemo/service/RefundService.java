package com.example.javabackenddemo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.CreateRefundRequest;
import com.example.javabackenddemo.dto.request.HandleRefundRequest;
import com.example.javabackenddemo.dto.response.RefundResponse;

public interface RefundService {
    RefundResponse createRefund(Long userId, CreateRefundRequest request);
    Page<RefundResponse> listUserRefunds(Long userId, int page, int size);
    RefundResponse getRefundDetail(Long refundId);
    Page<RefundResponse> adminListRefunds(String status, int page, int size);
    RefundResponse handleRefund(Long refundId, HandleRefundRequest request);
}
