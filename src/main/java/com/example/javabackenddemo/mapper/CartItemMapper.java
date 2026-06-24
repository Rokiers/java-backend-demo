package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javabackenddemo.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
