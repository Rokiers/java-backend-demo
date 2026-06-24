package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("user_coupon")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserCoupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(value = "coupon_id", jdbcType = JdbcType.BIGINT)
    private Long couponId;

    @TableField(jdbcType = JdbcType.BOOLEAN)
    @Builder.Default
    private Boolean used = false;

    @TableField(value = "used_order_id", jdbcType = JdbcType.BIGINT)
    private Long usedOrderId;

    @TableField(value = "used_at", jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime usedAt;

    @TableField(value = "claimed_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime claimedAt;
}
