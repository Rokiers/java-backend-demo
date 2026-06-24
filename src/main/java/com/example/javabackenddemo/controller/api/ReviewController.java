package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.CreateReviewRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.dto.response.ReviewResponse;
import com.example.javabackenddemo.security.SecurityUtils;
import com.example.javabackenddemo.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ApiResponse<ReviewResponse> create(@PathVariable Long productId, @Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.success(reviewService.createReview(SecurityUtils.getCurrentUserId(), productId, request));
    }

    @GetMapping
    public ApiResponse<PageResponse<ReviewResponse>> list(@PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(reviewService.listReviews(productId, page, size)));
    }
}
