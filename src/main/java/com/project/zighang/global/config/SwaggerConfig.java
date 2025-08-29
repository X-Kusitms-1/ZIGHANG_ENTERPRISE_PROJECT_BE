package com.project.zighang.global.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.local.server.url}")
    private String localServerUrl;

    @Value("${swagger.staging.server.url}")
    private String stagingServerUrl;

    @Value("${swagger.production.server.url}")
    private String productionServerUrl;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Zighang API Document")
                .version("v1.0.0")
                .description("직행 프로젝트의 API 명세서입니다.");

        Server localServer = new Server().url(localServerUrl).description("Local server");
        Server stgServer = new Server().url(stagingServerUrl).description("Staging server");
        Server prdServer = new Server().url(productionServerUrl).description("Production server");

        String jwtSchemeName = "jwtAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(info)
                .servers(List.of(localServer, stgServer, prdServer));
    }
}
