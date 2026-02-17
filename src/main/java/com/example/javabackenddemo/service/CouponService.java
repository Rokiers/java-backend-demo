package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.CreateCouponRequest;
import com.example.javabackenddemo.dto.response.CouponResponse;
import com.example.javabackenddemo.dto.response.UserCouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CreateCouponRequest request);
    Page<CouponResponse> listAvailableCoupons(Pageable pageable);
    UserCouponResponse claimCoupon(Long userId, Long couponId);
    List<UserCouponResponse> listUserCoupons(Long userId);
    BigDecimal applyCoupon(Long userId, Long userCouponId, BigDecimal orderAmount);
    void markCouponUsed(Long userCouponId, Long orderId);
}
