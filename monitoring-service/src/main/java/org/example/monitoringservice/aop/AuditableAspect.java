package org.example.monitoringservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.monitoringservice.configuration.AppPropertiesProvider;
import org.example.monitoringservice.configuration.AppProps;
import org.example.monitoringservice.factory.AuditComponentFactory;
import org.example.monitoringservice.factory.AuditComponentFactoryImpl;
import org.example.monitoringservice.model.audit.Action;
import org.example.monitoringservice.service.AuditService;

@Aspect
@Slf4j
public class AuditableAspect {

    private AuditService auditService;

    @Pointcut("@annotation(org.example.monitoringservice.aop.Auditable) && execution(* *(..))")
    public void annotatedByAuditable() {}

    @Around("annotatedByAuditable()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        log.info("Execution of method {} finished. Execution time is {} ms.", methodName, end);
        Action action = Action.builder().actionMethod(methodName).build();
        init();
        auditService.saveAction(action);
        return result;
    }

    private void init() {
        AppProps appProps = AppPropertiesProvider.getProperties();
        AuditComponentFactory auditComponentFactory =
                new AuditComponentFactoryImpl(appProps.getUrl(), appProps.getUserName(), appProps.getPassword());
        auditService = auditComponentFactory.createAuditService();
    }
}
