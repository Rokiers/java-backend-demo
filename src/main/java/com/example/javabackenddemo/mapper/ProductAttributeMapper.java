package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javabackenddemo.entity.ProductAttribute;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductAttributeMapper extends BaseMapper<ProductAttribute> {
}
