package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javabackenddemo.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
}
