package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.javabackenddemo.enums.RefundStatus;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("refund_request")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefundRequest {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "refund_no", jdbcType = JdbcType.VARCHAR)
    private String refundNo;

    @TableField(value = "order_id", jdbcType = JdbcType.BIGINT)
    private Long orderId;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(value = "refund_amount", jdbcType = JdbcType.DECIMAL)
    private BigDecimal refundAmount;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String reason;

    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String description;

    @TableField(jdbcType = JdbcType.VARCHAR)
    @Builder.Default
    private RefundStatus status = RefundStatus.PENDING;

    @TableField(value = "admin_remark", jdbcType = JdbcType.VARCHAR)
    private String adminRemark;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
