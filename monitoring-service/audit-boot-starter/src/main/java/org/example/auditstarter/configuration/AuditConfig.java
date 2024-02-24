package org.example.auditstarter.configuration;

import org.example.auditstarter.aop.AuditableAspect;
import org.example.auditstarter.repository.ActionRepository;
import org.example.auditstarter.repository.ActionRepositoryImpl;
import org.example.auditstarter.services.AuditService;
import org.example.auditstarter.services.AuditServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AuditConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriverName);
        return dataSource;
    }



    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public ActionRepository actionRepository() {
        return new ActionRepositoryImpl(jdbcTemplate());
    }

    @Bean
    public AuditService auditService() {
        return new AuditServiceImpl(actionRepository());
    }

    @Bean
    @ConditionalOnProperty("audit.enabled")
    public AuditableAspect auditableAspect() {
        return new AuditableAspect(auditService());
    }
}
