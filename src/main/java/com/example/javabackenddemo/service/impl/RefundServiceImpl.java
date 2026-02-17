package com.example.javabackenddemo.service.impl;

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
import com.example.javabackenddemo.repository.OrderRepository;
import com.example.javabackenddemo.repository.PaymentRepository;
import com.example.javabackenddemo.repository.RefundRequestRepository;
import com.example.javabackenddemo.service.RefundService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RefundServiceImpl implements RefundService {

    private final RefundRequestRepository refundRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public RefundServiceImpl(RefundRequestRepository refundRepository, OrderRepository orderRepository,
                             PaymentRepository paymentRepository) {
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public RefundResponse createRefund(Long userId, CreateRefundRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.SHIPPED
                && order.getStatus() != OrderStatus.COMPLETED) {
            throw new InvalidStateTransitionException("Order cannot be refunded in current status");
        }
        if (request.refundAmount().compareTo(order.getTotalAmount()) > 0) {
            throw new InvalidStateTransitionException("Refund amount exceeds order total");
        }
        refundRepository.findByOrderIdAndUserId(request.orderId(), userId)
                .ifPresent(r -> { throw new InvalidStateTransitionException("Refund already exists for this order"); });

        String refundNo = "REF" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
        RefundRequest refund = RefundRequest.builder()
                .refundNo(refundNo).orderId(order.getId()).userId(userId)
                .refundAmount(request.refundAmount()).reason(request.reason())
                .description(request.description()).build();
        return toResponse(refundRepository.save(refund));
    }

    @Override
    public Page<RefundResponse> listUserRefunds(Long userId, Pageable pageable) {
        return refundRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    @Override
    public RefundResponse getRefundDetail(Long refundId) {
        return toResponse(refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId)));
    }

    @Override
    public Page<RefundResponse> adminListRefunds(String status, Pageable pageable) {
        if (status != null) {
            return refundRepository.findByStatus(RefundStatus.valueOf(status), pageable).map(this::toResponse);
        }
        return refundRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public RefundResponse handleRefund(Long refundId, HandleRefundRequest request) {
        RefundRequest refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found: " + refundId));
        RefundStatus target = "APPROVE".equalsIgnoreCase(request.action())
                ? RefundStatus.APPROVED : RefundStatus.REJECTED;
        if (!refund.getStatus().canTransitionTo(target)) {
            throw new InvalidStateTransitionException("Cannot transition refund from " + refund.getStatus() + " to " + target);
        }
        refund.setStatus(target);
        if (request.adminRemark() != null) refund.setAdminRemark(request.adminRemark());
        if (target == RefundStatus.APPROVED) {
            // Mark payment as refunded
            paymentRepository.findByOrderIdAndStatus(refund.getOrderId(), PaymentStatus.SUCCESS)
                    .ifPresent(p -> { p.setStatus(PaymentStatus.REFUNDED); paymentRepository.save(p); });
            // Update order status to CANCELLED
            Order order = orderRepository.findById(refund.getOrderId()).orElse(null);
            if (order != null) { order.setStatus(OrderStatus.CANCELLED); orderRepository.save(order); }
        }
        return toResponse(refundRepository.save(refund));
    }

    private RefundResponse toResponse(RefundRequest r) {
        return new RefundResponse(r.getId(), r.getRefundNo(), r.getOrderId(), r.getRefundAmount(),
                r.getReason(), r.getDescription(), r.getStatus().name(), r.getAdminRemark(), r.getCreatedAt());
    }
}
