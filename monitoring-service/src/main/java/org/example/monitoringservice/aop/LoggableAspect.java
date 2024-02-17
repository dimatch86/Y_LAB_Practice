package org.example.monitoringservice.aop;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggableAspect {
    @Pointcut("@within(org.example.monitoringservice.aop.Auditable)")
    public void loggablePointcut() {}

    @Around("loggablePointcut()")
    public Object loggableAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        log.info("Execution of method {} finished. Execution time is {} ms.", methodName, end);
        return result;
    }
}
