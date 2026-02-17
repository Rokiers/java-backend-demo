package com.example.javabackenddemo.controller.admin;

import com.example.javabackenddemo.dto.request.CreateCouponRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CouponResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ApiResponse<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        return ApiResponse.success(couponService.createCoupon(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<CouponResponse>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(couponService.listAvailableCoupons(PageRequest.of(page, size))));
    }
}
