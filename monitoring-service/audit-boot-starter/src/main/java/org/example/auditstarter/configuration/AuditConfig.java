package org.example.auditstarter.configuration;

import org.example.auditstarter.aop.AuditableAspect;
import org.example.auditstarter.aop.EnableAudit;
import org.example.auditstarter.repository.ActionRepository;
import org.example.auditstarter.repository.ActionRepositoryImpl;
import org.example.auditstarter.services.AuditService;
import org.example.auditstarter.services.AuditServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnBean(annotation = EnableAudit.class)
public class AuditConfig {

    @Bean
    public ActionRepository actionRepository(JdbcTemplate jdbcTemplate) {
        return new ActionRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public AuditService auditService(ActionRepository actionRepository) {
        return new AuditServiceImpl(actionRepository);
    }

    @Bean
    public AuditableAspect auditableAspect(AuditService auditService) {
        return new AuditableAspect(auditService);
    }
}
