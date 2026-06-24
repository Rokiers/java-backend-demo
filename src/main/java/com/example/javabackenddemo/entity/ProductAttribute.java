package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

@TableName("product_attribute")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductAttribute {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "product_id", jdbcType = JdbcType.BIGINT)
    private Long productId;

    @TableField(value = "attr_name", jdbcType = JdbcType.VARCHAR)
    private String attrName;

    @TableField(value = "attr_value", jdbcType = JdbcType.VARCHAR)
    private String attrValue;
}
