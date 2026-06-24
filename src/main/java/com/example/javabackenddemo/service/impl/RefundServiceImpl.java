package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.CreateRefundRequest;
import com.example.javabackenddemo.dto.request.HandleRefundRequest;
import com.example.javabackenddemo.dto.response.RefundResponse;
import com.example.javabackenddemo.entity.Order;
import com.example.javabackenddemo.entity.Payment;
import com.example.javabackenddemo.entity.RefundRequest;
import com.example.javabackenddemo.enums.OrderStatus;
import com.example.javabackenddemo.enums.PaymentStatus;
import com.example.javabackenddemo.enums.RefundStatus;
import com.example.javabackenddemo.exception.InvalidStateTransitionException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.OrderMapper;
import com.example.javabackenddemo.mapper.PaymentMapper;
import com.example.javabackenddemo.mapper.RefundRequestMapper;
import com.example.javabackenddemo.service.RefundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RefundServiceImpl implements RefundService {

    private final RefundRequestMapper refundMapper;
    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;

    public RefundServiceImpl(RefundRequestMapper refundMapper, OrderMapper orderMapper,
                             PaymentMapper paymentMapper) {
        this.refundMapper = refundMapper;
        this.orderMapper = orderMapper;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Transactional
    public RefundResponse createRefund(Long userId, CreateRefundRequest request) {
        Order order = orderMapper.selectById(request.orderId());
        if (order == null) throw new ResourceNotFoundException("Order not found: " + request.orderId());
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.SHIPPED
                && order.getStatus() != OrderStatus.COMPLETED) {
            throw new InvalidStateTransitionException("Order cannot be refunded in current status");
        }
        if (request.refundAmount().compareTo(order.getTotalAmount()) > 0) {
            throw new InvalidStateTransitionException("Refund amount exceeds order total");
        }
        RefundRequest existing = refundMapper.selectOne(new LambdaQueryWrapper<RefundRequest>()
                .eq(RefundRequest::getOrderId, request.orderId()).eq(RefundRequest::getUserId, userId));
        if (existing != null) throw new InvalidStateTransitionException("Refund already exists for this order");

        String refundNo = "REF" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
        RefundRequest refund = RefundRequest.builder()
                .refundNo(refundNo).orderId(order.getId()).userId(userId)
                .refundAmount(request.refundAmount()).reason(request.reason())
                .description(request.description()).build();
        refundMapper.insert(refund);
        return toResponse(refund);
    }

    @Override
    public Page<RefundResponse> listUserRefunds(Long userId, int page, int size) {
        Page<RefundRequest> result = refundMapper.selectPage(new Page<>(page + 1, size),
                new LambdaQueryWrapper<RefundRequest>().eq(RefundRequest::getUserId, userId)
                        .orderByDesc(RefundRequest::getCreatedAt));
        return convertPage(result, this::toResponse);
    }

    @Override
    public RefundResponse getRefundDetail(Long refundId) {
        RefundRequest refund = refundMapper.selectById(refundId);
        if (refund == null) throw new ResourceNotFoundException("Refund not found: " + refundId);
        return toResponse(refund);
    }

    @Override
    public Page<RefundResponse> adminListRefunds(String status, int page, int size) {
        LambdaQueryWrapper<RefundRequest> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(RefundRequest::getStatus, RefundStatus.valueOf(status));
        wrapper.orderByDesc(RefundRequest::getCreatedAt);
        Page<RefundRequest> result = refundMapper.selectPage(new Page<>(page + 1, size), wrapper);
        return convertPage(result, this::toResponse);
    }

    @Override
    @Transactional
    public RefundResponse handleRefund(Long refundId, HandleRefundRequest request) {
        RefundRequest refund = refundMapper.selectById(refundId);
        if (refund == null) throw new ResourceNotFoundException("Refund not found: " + refundId);
        RefundStatus target = "APPROVE".equalsIgnoreCase(request.action())
                ? RefundStatus.APPROVED : RefundStatus.REJECTED;
        if (!refund.getStatus().canTransitionTo(target)) {
            throw new InvalidStateTransitionException("Cannot transition refund from " + refund.getStatus() + " to " + target);
        }
        refund.setStatus(target);
        if (request.adminRemark() != null) refund.setAdminRemark(request.adminRemark());
        if (target == RefundStatus.APPROVED) {
            Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                    .eq(Payment::getOrderId, refund.getOrderId()).eq(Payment::getStatus, PaymentStatus.SUCCESS));
            if (payment != null) { payment.setStatus(PaymentStatus.REFUNDED); paymentMapper.updateById(payment); }
            Order order = orderMapper.selectById(refund.getOrderId());
            if (order != null) { order.setStatus(OrderStatus.CANCELLED); orderMapper.updateById(order); }
        }
        refundMapper.updateById(refund);
        return toResponse(refund);
    }

    private RefundResponse toResponse(RefundRequest r) {
        return new RefundResponse(r.getId(), r.getRefundNo(), r.getOrderId(), r.getRefundAmount(),
                r.getReason(), r.getDescription(), r.getStatus().name(), r.getAdminRemark(), r.getCreatedAt());
    }

    @SuppressWarnings("unchecked")
    private <T, R> Page<R> convertPage(Page<T> source, java.util.function.Function<T, R> mapper) {
        Page<R> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(mapper).toList());
        return result;
    }
}
