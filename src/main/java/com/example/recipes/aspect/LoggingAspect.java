package com.example.recipes.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Логирование вызовов всех методов из пакета service
    @Before("execution(* com.example.recipes.service.*.*(..))")
    public void logMethodCall(JoinPoint joinPoint) {
        logger.info("Method called: {}", joinPoint.getSignature().toShortString());
    }

    // Логирование исключений, выбрасываемых методами в пакете service
    @AfterThrowing(pointcut = "execution(* com.example.recipes.service.*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        logger.error("Exception in method: {}", joinPoint.getSignature().toShortString(), ex);
    }
}
