package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

@TableName("product_translation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductTranslation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "product_id", jdbcType = JdbcType.BIGINT)
    private Long productId;

    @TableField(value = "language_code", jdbcType = JdbcType.VARCHAR)
    private String languageCode;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String name;

    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String description;
}
