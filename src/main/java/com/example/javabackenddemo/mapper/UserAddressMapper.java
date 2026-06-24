package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javabackenddemo.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
