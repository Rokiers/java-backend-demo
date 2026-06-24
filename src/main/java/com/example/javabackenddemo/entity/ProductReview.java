package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("product_review")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "product_id", jdbcType = JdbcType.BIGINT)
    private Long productId;

    @TableField(value = "order_id", jdbcType = JdbcType.BIGINT)
    private Long orderId;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(jdbcType = JdbcType.INTEGER)
    private Integer rating;

    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String content;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String images;

    @TableField(jdbcType = JdbcType.BOOLEAN)
    @Builder.Default
    private Boolean visible = true;

    @TableField(value = "admin_reply", jdbcType = JdbcType.LONGVARCHAR)
    private String adminReply;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
