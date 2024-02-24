package org.example.loggingstarter.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class LoggableAspect {

    @Pointcut("@within(org.example.loggingstarter.aop.Loggable)")
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
