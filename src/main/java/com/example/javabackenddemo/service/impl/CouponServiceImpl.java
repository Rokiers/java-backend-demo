package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.request.CreateCouponRequest;
import com.example.javabackenddemo.dto.response.CouponResponse;
import com.example.javabackenddemo.dto.response.UserCouponResponse;
import com.example.javabackenddemo.entity.Coupon;
import com.example.javabackenddemo.entity.UserCoupon;
import com.example.javabackenddemo.enums.CouponType;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.InvalidStateTransitionException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.CouponRepository;
import com.example.javabackenddemo.repository.UserCouponRepository;
import com.example.javabackenddemo.service.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponServiceImpl(CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {
        couponRepository.findByCode(request.code())
                .ifPresent(c -> { throw new DuplicateResourceException("Coupon code already exists: " + request.code()); });
        Coupon coupon = Coupon.builder()
                .name(request.name())
                .code(request.code())
                .couponType(CouponType.valueOf(request.couponType()))
                .discountValue(request.discountValue())
                .minOrderAmount(request.minOrderAmount() != null ? request.minOrderAmount() : BigDecimal.ZERO)
                .totalCount(request.totalCount())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();
        return toCouponResponse(couponRepository.save(coupon));
    }

    @Override
    public Page<CouponResponse> listAvailableCoupons(Pageable pageable) {
        return couponRepository.findAvailable(LocalDateTime.now(), pageable).map(this::toCouponResponse);
    }

    @Override
    @Transactional
    public UserCouponResponse claimCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + couponId));
        if (!coupon.getEnabled() || LocalDateTime.now().isBefore(coupon.getStartTime())
                || LocalDateTime.now().isAfter(coupon.getEndTime())) {
            throw new InvalidStateTransitionException("Coupon is not available");
        }
        if (coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new InvalidStateTransitionException("Coupon is fully claimed");
        }
        userCouponRepository.findByUserIdAndCouponId(userId, couponId)
                .ifPresent(uc -> { throw new DuplicateResourceException("Coupon already claimed"); });
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
        UserCoupon uc = UserCoupon.builder().userId(userId).couponId(couponId).build();
        return toUserCouponResponse(userCouponRepository.save(uc), coupon);
    }

    @Override
    public List<UserCouponResponse> listUserCoupons(Long userId) {
        return userCouponRepository.findByUserId(userId).stream().map(uc -> {
            Coupon coupon = couponRepository.findById(uc.getCouponId()).orElse(null);
            return toUserCouponResponse(uc, coupon);
        }).toList();
    }

    @Override
    public BigDecimal applyCoupon(Long userId, Long userCouponId, BigDecimal orderAmount) {
        UserCoupon uc = userCouponRepository.findByIdAndUserIdAndUsedFalse(userCouponId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User coupon not found or already used"));
        Coupon coupon = couponRepository.findById(uc.getCouponId())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        if (orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new InvalidStateTransitionException("Order amount does not meet minimum requirement");
        }
        if (coupon.getCouponType() == CouponType.FIXED_AMOUNT) {
            BigDecimal result = orderAmount.subtract(coupon.getDiscountValue());
            return result.compareTo(BigDecimal.ZERO) > 0 ? result : BigDecimal.ZERO;
        } else {
            BigDecimal discount = orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return orderAmount.subtract(discount);
        }
    }

    @Override
    @Transactional
    public void markCouponUsed(Long userCouponId, Long orderId) {
        UserCoupon uc = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ResourceNotFoundException("User coupon not found"));
        uc.setUsed(true);
        uc.setUsedOrderId(orderId);
        uc.setUsedAt(LocalDateTime.now());
        userCouponRepository.save(uc);
    }

    private CouponResponse toCouponResponse(Coupon c) {
        return new CouponResponse(c.getId(), c.getName(), c.getCode(), c.getCouponType().name(),
                c.getDiscountValue(), c.getMinOrderAmount(), c.getTotalCount(), c.getUsedCount(),
                c.getStartTime(), c.getEndTime(), c.getEnabled());
    }

    private UserCouponResponse toUserCouponResponse(UserCoupon uc, Coupon coupon) {
        return new UserCouponResponse(uc.getId(), uc.getCouponId(),
                coupon != null ? coupon.getName() : "", coupon != null ? coupon.getCouponType().name() : "",
                coupon != null ? coupon.getDiscountValue() : BigDecimal.ZERO,
                coupon != null ? coupon.getMinOrderAmount() : BigDecimal.ZERO,
                uc.getUsed(), uc.getClaimedAt(), coupon != null ? coupon.getEndTime() : null);
    }
}
