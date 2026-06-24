package com.example.javabackenddemo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.AdminReplyReviewRequest;
import com.example.javabackenddemo.dto.request.CreateReviewRequest;
import com.example.javabackenddemo.dto.response.ReviewResponse;

public interface ReviewService {
    ReviewResponse createReview(Long userId, Long productId, CreateReviewRequest request);
    Page<ReviewResponse> listReviews(Long productId, int page, int size);
    Page<ReviewResponse> adminListReviews(Long productId, int page, int size);
    ReviewResponse adminReply(Long reviewId, AdminReplyReviewRequest request);
    ReviewResponse toggleVisibility(Long reviewId);
}
