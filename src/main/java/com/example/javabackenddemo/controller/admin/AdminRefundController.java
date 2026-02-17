package com.example.javabackenddemo.controller.admin;

import com.example.javabackenddemo.dto.request.HandleRefundRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.dto.response.RefundResponse;
import com.example.javabackenddemo.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/refunds")
public class AdminRefundController {

    private final RefundService refundService;

    public AdminRefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @GetMapping
    public ApiResponse<PageResponse<RefundResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(refundService.adminListRefunds(status, PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ApiResponse<RefundResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(refundService.getRefundDetail(id));
    }

    @PutMapping("/{id}/handle")
    public ApiResponse<RefundResponse> handle(@PathVariable Long id,
            @Valid @RequestBody HandleRefundRequest request) {
        return ApiResponse.success(refundService.handleRefund(id, request));
    }
}
