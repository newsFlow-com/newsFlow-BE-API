package com.newsflow.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtScheme = "bearerAuth";
        String apiKeyScheme = "X-API-Key";

        return new OpenAPI()
                .info(new Info()
                        .title("NewsFlow API")
                        .description("NewsFlow 뉴스 큐레이션 플랫폼 API — 사용자/관리자 게이트 분리")
                        .version("v1.0"))
                .addSecurityItem(new SecurityRequirement().addList(jwtScheme))
                .components(new Components()
                        .addSecuritySchemes(jwtScheme, new SecurityScheme()
                                .name(jwtScheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        .addSecuritySchemes(apiKeyScheme, new SecurityScheme()
                                .name("X-API-Key")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("B2B API 전용 키. /api/admin/v1/api-keys 에서 발급")));
    }
}
