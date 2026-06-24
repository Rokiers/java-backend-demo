package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("`user`")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "username", jdbcType = JdbcType.VARCHAR)
    private String username;

    @TableField(value = "password", jdbcType = JdbcType.VARCHAR)
    private String password;

    @TableField(value = "email", jdbcType = JdbcType.VARCHAR)
    private String email;

    @TableField(value = "phone", jdbcType = JdbcType.VARCHAR)
    private String phone;

    @TableField(value = "role", jdbcType = JdbcType.VARCHAR)
    @Builder.Default
    private String role = "ROLE_USER";

    @TableField(value = "enabled", jdbcType = JdbcType.BOOLEAN)
    @Builder.Default
    private Boolean enabled = true;

    @TableField(value = "created_at", fill = FieldFill.INSERT, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
