package org.example.auditstarter.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.example.auditstarter.model.Action;
import org.example.auditstarter.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

@Aspect
@Slf4j
public class AuditableAspect {
    private final AuditService auditService;

    @Autowired
    public AuditableAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Pointcut("execution(public * *..service.*.*(..))")
    public void auditablePointcut() {}

    @AfterReturning("auditablePointcut()")
    public void auditableAdvice(JoinPoint joinPoint) {
        String username = "anonymous";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        String methodName = joinPoint.getSignature().getName();
        auditService.saveAction(new Action(methodName, username, Instant.now()));
        log.info("Audit aspect has been executed");
    }
}
