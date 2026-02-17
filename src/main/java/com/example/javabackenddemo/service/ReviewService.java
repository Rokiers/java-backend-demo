package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.AdminReplyReviewRequest;
import com.example.javabackenddemo.dto.request.CreateReviewRequest;
import com.example.javabackenddemo.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request);
    Page<ReviewResponse> listReviews(Long productId, Pageable pageable);
    Page<ReviewResponse> adminListReviews(Long productId, Pageable pageable);
    ReviewResponse adminReply(Long reviewId, AdminReplyReviewRequest request);
    ReviewResponse toggleVisibility(Long reviewId);
}
