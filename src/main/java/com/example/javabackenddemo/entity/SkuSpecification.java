package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

@TableName("sku_specification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SkuSpecification {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "sku_id", jdbcType = JdbcType.BIGINT)
    private Long skuId;

    @TableField(value = "spec_name", jdbcType = JdbcType.VARCHAR)
    private String specName;

    @TableField(value = "spec_value", jdbcType = JdbcType.VARCHAR)
    private String specValue;
}
