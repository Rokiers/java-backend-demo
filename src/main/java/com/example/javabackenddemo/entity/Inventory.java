package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "sku_id", jdbcType = JdbcType.BIGINT)
    private Long skuId;

    @TableField(jdbcType = JdbcType.INTEGER)
    @Builder.Default
    private Integer quantity = 0;

    @TableField(value = "alert_threshold", jdbcType = JdbcType.INTEGER)
    @Builder.Default
    private Integer alertThreshold = 10;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
