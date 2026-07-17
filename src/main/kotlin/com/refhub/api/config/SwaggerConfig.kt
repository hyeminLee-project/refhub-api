package com.refhub.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("RefHub API")
                .description("AI/개발 레퍼런스 수집·분류·검색 플랫폼 API")
                .version("0.1.0")
                .contact(Contact().name("hyemin").url("https://github.com/hyeminLee-project"))
        )
        .addSecurityItem(SecurityRequirement().addList("Bearer"))
        .components(
            Components().addSecuritySchemes(
                "Bearer",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
        )
}
