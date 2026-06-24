package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("user_address")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAddress {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id", jdbcType = JdbcType.BIGINT)
    private Long userId;

    @TableField(value = "receiver_name", jdbcType = JdbcType.VARCHAR)
    private String receiverName;

    @TableField(value = "receiver_phone", jdbcType = JdbcType.VARCHAR)
    private String receiverPhone;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String province;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String city;

    @TableField(jdbcType = JdbcType.VARCHAR)
    private String district;

    @TableField(value = "detail_address", jdbcType = JdbcType.VARCHAR)
    private String detailAddress;

    @TableField(value = "is_default", jdbcType = JdbcType.BOOLEAN)
    @Builder.Default
    private Boolean isDefault = false;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
