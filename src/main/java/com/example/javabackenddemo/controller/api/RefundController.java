package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.CreateRefundRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.dto.response.RefundResponse;
import com.example.javabackenddemo.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public ApiResponse<RefundResponse> create(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateRefundRequest request) {
        return ApiResponse.success(refundService.createRefund(userId, request));
    }

    @GetMapping
    public ApiResponse<PageResponse<RefundResponse>> list(@RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(refundService.listUserRefunds(userId, PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ApiResponse<RefundResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(refundService.getRefundDetail(id));
    }
}
