package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.javabackenddemo.enums.PaymentMethod;
import com.example.javabackenddemo.enums.PaymentStatus;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "payment_no", jdbcType = JdbcType.VARCHAR)
    private String paymentNo;

    @TableField(value = "order_id", jdbcType = JdbcType.BIGINT)
    private Long orderId;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(jdbcType = JdbcType.DECIMAL)
    private BigDecimal amount;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String currency;

    @TableField(jdbcType = JdbcType.VARCHAR)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @TableField(value = "payment_method", jdbcType = JdbcType.VARCHAR)
    private PaymentMethod paymentMethod;

    @TableField(value = "paid_at", jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime paidAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
