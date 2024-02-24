package org.example.monitoringservice.configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.configuration.properties.SwaggerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title(swaggerProperties.getTitle())
                        .version(swaggerProperties.getVersion())
                        .description(swaggerProperties.getDescription())
                        .license(new License().name(swaggerProperties.getLicense().getName())
                                .url(swaggerProperties.getLicense().getUrl()))
                        .contact(new Contact().name(swaggerProperties.getContact().getName())
                                .email(swaggerProperties.getContact().getEmail())))
                .servers(List.of(new Server().url(swaggerProperties.getServer().getUrl())));
    }
}
