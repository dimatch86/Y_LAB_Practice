package org.example.loggingstarter.configuration;

import org.example.loggingstarter.aop.LoggableAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public LoggableAspect loggableAspect() {
        return new LoggableAspect();
    }
}
