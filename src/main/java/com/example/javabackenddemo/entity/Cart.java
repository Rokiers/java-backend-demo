package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("cart")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
