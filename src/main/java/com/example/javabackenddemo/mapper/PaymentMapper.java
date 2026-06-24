package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javabackenddemo.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
