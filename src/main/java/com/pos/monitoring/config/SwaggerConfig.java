package com.pos.monitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    // TODO: 4/14/2023 loggingni korish kerak ekan

    @Bean
    public OpenAPI api() {
        Server server = new Server();
        server.setUrl("/");
        return new OpenAPI().servers(List.of(server)).security(requirements()).schemaRequirement("Security schema", requirement()).info(info());
    }


    private Info info() {
        return new Info().title("GEO STAT API").version("v1");
    }

    private SecurityScheme requirement() {
        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.scheme("Bearer");
        securityScheme.type(SecurityScheme.Type.HTTP);
        securityScheme.in(SecurityScheme.In.HEADER);
        securityScheme.bearerFormat("JWT");
        return securityScheme;
    }

    private List<SecurityRequirement> requirements() {
        return Collections.singletonList(new SecurityRequirement().addList("Security schema"));
    }

}
