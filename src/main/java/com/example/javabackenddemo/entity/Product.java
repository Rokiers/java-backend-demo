package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.javabackenddemo.enums.ProductStatus;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("product")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String name;

    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String description;

    @TableField(value = "main_image", jdbcType = JdbcType.VARCHAR)
    private String mainImage;

    @TableField(value = "category_id", jdbcType = JdbcType.BIGINT)
    private Long categoryId;

    @TableField(jdbcType = JdbcType.VARCHAR)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @TableField(value = "base_currency", jdbcType = JdbcType.VARCHAR)
    @Builder.Default
    private String baseCurrency = "CNY";

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
