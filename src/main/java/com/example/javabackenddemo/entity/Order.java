package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.javabackenddemo.enums.OrderStatus;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "order_no", jdbcType = JdbcType.VARCHAR)
    private String orderNo;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(value = "total_amount", jdbcType = JdbcType.DECIMAL)
    private BigDecimal totalAmount;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String currency;

    @TableField(jdbcType = JdbcType.VARCHAR)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @TableField(value = "shipping_address", jdbcType = JdbcType.LONGVARCHAR)
    private String shippingAddress;

    @TableField(value = "tracking_number", jdbcType = JdbcType.VARCHAR)
    private String trackingNumber;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
