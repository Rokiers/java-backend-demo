package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.javabackenddemo.enums.ProductStatus;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("sku")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sku {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "product_id", jdbcType = JdbcType.BIGINT)
    private Long productId;

    @TableField(value = "sku_code", jdbcType = JdbcType.VARCHAR)
    private String skuCode;

    @TableField(jdbcType = JdbcType.DECIMAL)
    private BigDecimal price;

    @TableField(jdbcType = JdbcType.VARCHAR)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;
}
