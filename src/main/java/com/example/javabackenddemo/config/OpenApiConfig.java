package com.example.javabackenddemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("电商商城 API")
                        .version("1.0.0")
                        .description("电商商城后台管理系统 API 文档，包含前台商城接口和后台管理接口")
                        .contact(new Contact()
                                .name("Admin")
                                .email("admin@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("本地开发环境")));
    }
}
