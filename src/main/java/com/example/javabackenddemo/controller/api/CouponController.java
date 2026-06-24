package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CouponResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.dto.response.UserCouponResponse;
import com.example.javabackenddemo.security.SecurityUtils;
import com.example.javabackenddemo.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CouponResponse>> listAvailable(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(couponService.listAvailableCoupons(page, size)));
    }

    @PostMapping("/{couponId}/claim")
    public ApiResponse<UserCouponResponse> claim(@PathVariable Long couponId) {
        return ApiResponse.success(couponService.claimCoupon(SecurityUtils.getCurrentUserId(), couponId));
    }

    @GetMapping("/mine")
    public ApiResponse<List<UserCouponResponse>> myCoupons() {
        return ApiResponse.success(couponService.listUserCoupons(SecurityUtils.getCurrentUserId()));
    }
}
