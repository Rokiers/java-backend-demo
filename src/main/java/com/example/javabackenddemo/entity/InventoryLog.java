package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.javabackenddemo.enums.InventoryChangeType;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("inventory_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "inventory_id", jdbcType = JdbcType.BIGINT)
    private Long inventoryId;

    @TableField(value = "change_quantity", jdbcType = JdbcType.INTEGER)
    private Integer changeQuantity;

    @TableField(value = "after_quantity", jdbcType = JdbcType.INTEGER)
    private Integer afterQuantity;

    @TableField(value = "change_type", jdbcType = JdbcType.VARCHAR)
    private InventoryChangeType changeType;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String remark;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;
}
