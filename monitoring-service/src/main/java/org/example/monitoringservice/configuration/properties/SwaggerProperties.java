package org.example.monitoringservice.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "swagger")
@Getter
@Setter
public class SwaggerProperties {
    private String title;
    private String version;
    private String description;
    @NestedConfigurationProperty
    private License license;

    @NestedConfigurationProperty
    private Contact contact;
    @NestedConfigurationProperty
    private Server server;
}
