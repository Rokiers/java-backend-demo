package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.request.CreateCouponRequest;
import com.example.javabackenddemo.dto.response.CouponResponse;
import com.example.javabackenddemo.dto.response.UserCouponResponse;
import com.example.javabackenddemo.entity.Coupon;
import com.example.javabackenddemo.entity.UserCoupon;
import com.example.javabackenddemo.enums.CouponType;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.InvalidStateTransitionException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.CouponMapper;
import com.example.javabackenddemo.mapper.UserCouponMapper;
import com.example.javabackenddemo.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    public CouponServiceImpl(CouponMapper couponMapper, UserCouponMapper userCouponMapper) {
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
    }

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {
        if (couponMapper.selectOne(new LambdaQueryWrapper<Coupon>().eq(Coupon::getCode, request.code())) != null) {
            throw new DuplicateResourceException("Coupon code already exists: " + request.code());
        }
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
        couponMapper.insert(coupon);
        return toCouponResponse(coupon);
    }

    @Override
    public Page<CouponResponse> listAvailableCoupons(int page, int size) {
        List<Coupon> all = couponMapper.findAvailable();
        Page<CouponResponse> result = new Page<>(page + 1, size, all.size());
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        result.setRecords(all.subList(from, to).stream().map(this::toCouponResponse).toList());
        return result;
    }

    @Override
    @Transactional
    public UserCouponResponse claimCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) throw new ResourceNotFoundException("Coupon not found: " + couponId);
        if (!coupon.getEnabled() || LocalDateTime.now().isBefore(coupon.getStartTime())
                || LocalDateTime.now().isAfter(coupon.getEndTime())) {
            throw new InvalidStateTransitionException("Coupon is not available");
        }
        if (coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new InvalidStateTransitionException("Coupon is fully claimed");
        }
        if (userCouponMapper.selectOne(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId).eq(UserCoupon::getCouponId, couponId)) != null) {
            throw new DuplicateResourceException("Coupon already claimed");
        }
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponMapper.updateById(coupon);
        UserCoupon uc = UserCoupon.builder().userId(userId).couponId(couponId).build();
        userCouponMapper.insert(uc);
        return toUserCouponResponse(uc, coupon);
    }

    @Override
    public List<UserCouponResponse> listUserCoupons(Long userId) {
        return userCouponMapper.selectList(new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getUserId, userId))
                .stream().map(uc -> {
                    Coupon coupon = couponMapper.selectById(uc.getCouponId());
                    return toUserCouponResponse(uc, coupon);
                }).toList();
    }

    @Override
    public BigDecimal applyCoupon(Long userId, Long userCouponId, BigDecimal orderAmount) {
        UserCoupon uc = userCouponMapper.selectOne(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getId, userCouponId).eq(UserCoupon::getUserId, userId).eq(UserCoupon::getUsed, false));
        if (uc == null) throw new ResourceNotFoundException("User coupon not found or already used");
        Coupon coupon = couponMapper.selectById(uc.getCouponId());
        if (coupon == null) throw new ResourceNotFoundException("Coupon not found");
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
        UserCoupon uc = userCouponMapper.selectById(userCouponId);
        if (uc == null) throw new ResourceNotFoundException("User coupon not found");
        uc.setUsed(true);
        uc.setUsedOrderId(orderId);
        uc.setUsedAt(LocalDateTime.now());
        userCouponMapper.updateById(uc);
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
