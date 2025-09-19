package com.room_reservation.config;

import io.swagger.v3.oas.models.OpenAPI;

import io.swagger.v3.oas.models.info.Info;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API 기본 정보
                .info(new Info()
                        .title("회의실 예약 시스템 API")
                        .description("회의실 예약 및 관리 시스템의 REST API 문서")
                        .version("1.0.0")
                )
                // 서버 정보
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                ))
                // Security 설정
                .addSecurityItem(new SecurityRequirement().addList("Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY) 
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description(
                                                "Authorization 헤더에 토큰을 포함하여 요청합니다.\n" +
                                                        "- ADMIN: `admin-token`\n" +
                                                        "- USER: `user-token-{id}`"))
                        );

    }
}
