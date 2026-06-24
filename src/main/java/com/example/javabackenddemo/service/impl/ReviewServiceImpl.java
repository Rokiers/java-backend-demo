package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.AdminReplyReviewRequest;
import com.example.javabackenddemo.dto.request.CreateReviewRequest;
import com.example.javabackenddemo.dto.response.ReviewResponse;
import com.example.javabackenddemo.entity.Order;
import com.example.javabackenddemo.entity.ProductReview;
import com.example.javabackenddemo.enums.OrderStatus;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.InvalidStateTransitionException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.OrderMapper;
import com.example.javabackenddemo.mapper.ProductReviewMapper;
import com.example.javabackenddemo.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewMapper reviewMapper;
    private final OrderMapper orderMapper;

    public ReviewServiceImpl(ProductReviewMapper reviewMapper, OrderMapper orderMapper) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request) {
        Order order = orderMapper.selectById(request.orderId());
        if (order == null) throw new ResourceNotFoundException("Order not found: " + request.orderId());
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new InvalidStateTransitionException("Can only review completed orders");
        }
        if (reviewMapper.selectCount(new LambdaQueryWrapper<ProductReview>()
                .eq(ProductReview::getOrderId, request.orderId()).eq(ProductReview::getUserId, userId)) > 0) {
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
        reviewMapper.insert(review);
        return toResponse(review);
    }

    @Override
    public Page<ReviewResponse> listReviews(Long productId, int page, int size) {
        Page<ProductReview> result = reviewMapper.selectPage(new Page<>(page + 1, size),
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getProductId, productId)
                        .eq(ProductReview::getVisible, true)
                        .orderByDesc(ProductReview::getCreatedAt));
        return convertPage(result, this::toResponse);
    }

    @Override
    public Page<ReviewResponse> adminListReviews(Long productId, int page, int size) {
        Page<ProductReview> result = reviewMapper.selectPage(new Page<>(page + 1, size),
                new LambdaQueryWrapper<ProductReview>().eq(ProductReview::getProductId, productId)
                        .orderByDesc(ProductReview::getCreatedAt));
        return convertPage(result, this::toResponse);
    }

    @Override
    public ReviewResponse adminReply(Long reviewId, AdminReplyReviewRequest request) {
        ProductReview review = reviewMapper.selectById(reviewId);
        if (review == null) throw new ResourceNotFoundException("Review not found: " + reviewId);
        review.setAdminReply(request.reply());
        reviewMapper.updateById(review);
        return toResponse(review);
    }

    @Override
    public ReviewResponse toggleVisibility(Long reviewId) {
        ProductReview review = reviewMapper.selectById(reviewId);
        if (review == null) throw new ResourceNotFoundException("Review not found: " + reviewId);
        review.setVisible(!review.getVisible());
        reviewMapper.updateById(review);
        return toResponse(review);
    }

    private ReviewResponse toResponse(ProductReview r) {
        return new ReviewResponse(r.getId(), r.getProductId(), r.getUserId(), r.getRating(),
                r.getContent(), r.getImages(), r.getAdminReply(), r.getVisible(), r.getCreatedAt());
    }

    @SuppressWarnings("unchecked")
    private <T, R> Page<R> convertPage(Page<T> source, java.util.function.Function<T, R> mapper) {
        Page<R> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(mapper).toList());
        return result;
    }
}
