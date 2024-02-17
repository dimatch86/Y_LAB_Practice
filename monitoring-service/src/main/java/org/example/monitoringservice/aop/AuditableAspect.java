package org.example.monitoringservice.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.monitoringservice.model.audit.Action;
import org.example.monitoringservice.service.AuditService;
import org.example.monitoringservice.util.UserContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditableAspect {

    private final AuditService auditService;

    @Pointcut("@within(org.example.monitoringservice.aop.Auditable)")
    public void auditablePointcut() {}

    @AfterReturning("auditablePointcut()")
    public void auditableAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Action action = Action.builder()
                .actionMethod(methodName)
                .actionedBy(UserContext.getCurrentUser() != null ?
                        UserContext.getCurrentUser().getEmail() : "NotDefinedUser")
                .build();
        auditService.saveAction(action);
        log.info("Audit aspect has been executed");
    }
}
