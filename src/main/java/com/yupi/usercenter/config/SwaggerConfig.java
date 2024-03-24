package com.yupi.usercenter.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev","test"})
public class SwaggerConfig {
    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI()
                .info(new Info().title("伙伴")
                        .description("伙伴")
                        .version("v1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("设计文档")
                        .url("aaa"));
    }
}