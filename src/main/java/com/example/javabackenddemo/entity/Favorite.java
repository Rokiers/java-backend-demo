package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("favorite")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(value = "product_id", jdbcType = JdbcType.BIGINT)
    private Long productId;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;
}
