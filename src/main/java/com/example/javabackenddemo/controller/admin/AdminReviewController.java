package com.example.javabackenddemo.controller.admin;

import com.example.javabackenddemo.dto.request.AdminReplyReviewRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.dto.response.ReviewResponse;
import com.example.javabackenddemo.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<PageResponse<ReviewResponse>> list(@PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(reviewService.adminListReviews(productId, PageRequest.of(page, size))));
    }

    @PutMapping("/{reviewId}/reply")
    public ApiResponse<ReviewResponse> reply(@PathVariable Long reviewId,
            @Valid @RequestBody AdminReplyReviewRequest request) {
        return ApiResponse.success(reviewService.adminReply(reviewId, request));
    }

    @PutMapping("/{reviewId}/visibility")
    public ApiResponse<ReviewResponse> toggleVisibility(@PathVariable Long reviewId) {
        return ApiResponse.success(reviewService.toggleVisibility(reviewId));
    }
}
