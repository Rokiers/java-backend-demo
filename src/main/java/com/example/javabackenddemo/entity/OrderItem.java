package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;

@TableName("order_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "order_id", jdbcType = JdbcType.BIGINT)
    private Long orderId;

    @TableField(value = "sku_id", jdbcType = JdbcType.BIGINT)
    private Long skuId;

    @TableField(value = "product_name_snapshot", jdbcType = JdbcType.VARCHAR)
    private String productNameSnapshot;

    @TableField(value = "sku_spec_snapshot", jdbcType = JdbcType.VARCHAR)
    private String skuSpecSnapshot;

    @TableField(value = "unit_price", jdbcType = JdbcType.DECIMAL)
    private BigDecimal unitPrice;

    @TableField(jdbcType = JdbcType.INTEGER)
    private Integer quantity;

    @TableField(jdbcType = JdbcType.DECIMAL)
    private BigDecimal subtotal;
}
