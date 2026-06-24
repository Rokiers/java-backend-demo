package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.javabackenddemo.enums.CouponType;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("coupon")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String name;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String code;

    @TableField(value = "coupon_type", jdbcType = JdbcType.VARCHAR)
    private CouponType couponType;

    @TableField(value = "discount_value", jdbcType = JdbcType.DECIMAL)
    private BigDecimal discountValue;

    @TableField(value = "min_order_amount", jdbcType = JdbcType.DECIMAL)
    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @TableField(value = "total_count", jdbcType = JdbcType.INTEGER)
    private Integer totalCount;

    @TableField(value = "used_count", jdbcType = JdbcType.INTEGER)
    @Builder.Default
    private Integer usedCount = 0;

    @TableField(value = "start_time", jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime startTime;

    @TableField(value = "end_time", jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime endTime;

    @TableField(jdbcType = JdbcType.BOOLEAN)
    @Builder.Default
    private Boolean enabled = true;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;
}
