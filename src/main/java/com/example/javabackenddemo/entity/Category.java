package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String name;

    @TableField(value = "parent_id", jdbcType = JdbcType.BIGINT)
    private Long parentId;

    @TableField(value = "sort", jdbcType = JdbcType.INTEGER)
    @Builder.Default
    private Integer sort = 0;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
