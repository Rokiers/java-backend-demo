package com.example.javabackenddemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("currency_rate")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CurrencyRate {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "base_currency", jdbcType = JdbcType.VARCHAR)
    private String baseCurrency;

    @TableField(value = "target_currency", jdbcType = JdbcType.VARCHAR)
    private String targetCurrency;

    @TableField(jdbcType = JdbcType.DECIMAL)
    private BigDecimal rate;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime updatedAt;
}
