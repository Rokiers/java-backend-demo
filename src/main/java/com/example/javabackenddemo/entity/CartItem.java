package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("cart_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "cart_id", jdbcType = JdbcType.BIGINT)
    private Long cartId;

    @TableField(value = "sku_id", jdbcType = JdbcType.BIGINT)
    private Long skuId;

    @TableField(jdbcType = JdbcType.INTEGER)
    private Integer quantity;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
