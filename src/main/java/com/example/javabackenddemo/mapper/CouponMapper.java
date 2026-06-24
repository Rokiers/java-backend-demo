package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javabackenddemo.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    @Select("SELECT * FROM coupon WHERE enabled = true AND start_time <= NOW() AND end_time >= NOW() AND used_count < total_count")
    List<Coupon> findAvailable();
}
