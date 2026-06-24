package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.entity.Product;
import com.example.javabackenddemo.enums.ProductStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT * FROM product WHERE status = #{status} AND (name LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%'))")
    Page<Product> searchByKeyword(Page<Product> page, @Param("status") String status, @Param("keyword") String keyword);

    @Select("SELECT COUNT(*) > 0 FROM product WHERE category_id = #{categoryId}")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId);
}
