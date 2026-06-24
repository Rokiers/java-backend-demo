package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.CreateRefundRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.dto.response.RefundResponse;
import com.example.javabackenddemo.security.SecurityUtils;
import com.example.javabackenddemo.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public ApiResponse<RefundResponse> create(@Valid @RequestBody CreateRefundRequest request) {
        return ApiResponse.success(refundService.createRefund(SecurityUtils.getCurrentUserId(), request));
    }

    @GetMapping
    public ApiResponse<PageResponse<RefundResponse>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(refundService.listUserRefunds(SecurityUtils.getCurrentUserId(), page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<RefundResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(refundService.getRefundDetail(id));
    }
}
