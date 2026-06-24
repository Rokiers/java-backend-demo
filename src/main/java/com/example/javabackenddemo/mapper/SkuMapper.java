package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javabackenddemo.entity.Sku;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkuMapper extends BaseMapper<Sku> {
}
