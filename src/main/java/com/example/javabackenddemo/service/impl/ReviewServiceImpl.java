package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.AdminReplyReviewRequest;
import com.example.javabackenddemo.dto.request.CreateReviewRequest;
import com.example.javabackenddemo.dto.response.ReviewResponse;
import com.example.javabackenddemo.entity.Order;
import com.example.javabackenddemo.entity.ProductReview;
import com.example.javabackenddemo.enums.OrderStatus;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.InvalidStateTransitionException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.OrderRepository;
import com.example.javabackenddemo.repository.ProductReviewRepository;
import com.example.javabackenddemo.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewServiceImpl(ProductReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new InvalidStateTransitionException("Can only review completed orders");
        }
        if (reviewRepository.existsByOrderIdAndUserId(request.orderId(), userId)) {
            throw new DuplicateResourceException("You have already reviewed this order");
        }
        ProductReview review = ProductReview.builder()
                .productId(productId)
                .orderId(request.orderId())
                .userId(userId)
                .rating(request.rating())
                .content(request.content())
                .images(request.images())
                .build();
        return toResponse(reviewRepository.save(review));
    }

    @Override
    public Page<ReviewResponse> listReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndVisibleTrue(productId, pageable).map(this::toResponse);
    }

    @Override
    public Page<ReviewResponse> adminListReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable).map(this::toResponse);
    }

    @Override
    public ReviewResponse adminReply(Long reviewId, AdminReplyReviewRequest request) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        review.setAdminReply(request.reply());
        return toResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse toggleVisibility(Long reviewId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        review.setVisible(!review.getVisible());
        return toResponse(reviewRepository.save(review));
    }

    private ReviewResponse toResponse(ProductReview r) {
        return new ReviewResponse(r.getId(), r.getProductId(), r.getUserId(), r.getRating(),
                r.getContent(), r.getImages(), r.getAdminReply(), r.getVisible(), r.getCreatedAt());
    }
}
