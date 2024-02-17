package org.example.monitoringservice.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@EnableAspectJAutoProxy
@Configuration
@ComponentScan(basePackages = {"org.springdoc"})
@Import({org.springdoc.core.configuration.SpringDocConfiguration.class, org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration.class,
        org.springdoc.webmvc.ui.SwaggerConfig.class,
        org.springdoc.core.properties.SwaggerUiConfigProperties.class,
        org.springdoc.core.properties.SwaggerUiOAuthProperties.class,
        org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class})
public class WebConfig implements WebMvcConfigurer {


}
