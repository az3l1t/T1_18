package net.az3l1t.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import net.az3l1t.aop.exception.TaskNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class TaskAspect {

    @Value("${execution.time.cracking:500}")
    private long crackingTime;

    @Before("@annotation(net.az3l1t.aop.aspect.annotation.LogExecution)")
    public void logExecutionBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Method {} called with arguments: {}", methodName, Arrays.toString(args));
    }

    @AfterThrowing(
            pointcut = "@annotation(net.az3l1t.aop.aspect.annotation.LogExceptionTaskNotFound)",
            throwing = "exception"
    )
    public void handleException(JoinPoint joinPoint, TaskNotFoundException exception) {
        log.error("Exception in method {}: {} - {}",
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }

    @AfterReturning(
            pointcut = "@annotation(net.az3l1t.aop.aspect.annotation.LogResult)",
            returning = "result"
    )
    public void afterReturningLogging(Object result) {
        log.info("Method returned instance of {} with content: {}",
                result.getClass().getSimpleName(),
                result
        );
    }

    @Around("@annotation(net.az3l1t.aop.aspect.annotation.LogTracking)")
    public Object executionTimeTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        if (duration > crackingTime) {
            log.warn("Method {} took {} ms (SLOW)", joinPoint.getSignature().getName(), duration);
        } else {
            log.info("Method {} executed in {} ms", joinPoint.getSignature().getName(), duration);
        }

        return proceed;
    }
}
