package com.example.javabackenddemo.config;

import org.springframework.context.annotation.Configuration;

/**
 * Jackson 3.x (Spring Boot 4.0.2) 自动配置说明：
 * - JsonMapper 已自动注册为 Bean，内置支持 Java 8 时间类型（LocalDateTime 等）
 * - 日期默认序列化为 ISO-8601 字符串格式，无需额外配置
 * - 时区跟随 JVM 默认时区（可通过 JVM 参数 -Duser.timezone=Asia/Shanghai 设置）
 */
@Configuration
public class JacksonConfig {
    // Jackson 3.x + Spring Boot 4.0.2 自动配置已足够，无需手动配置
}
