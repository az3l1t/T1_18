package net.az3l1t.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import net.az3l1t.aop.dto.TaskResponseDto;
import net.az3l1t.aop.exception.TaskNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Slf4j
public class TaskAspect {

    @Before("@annotation(net.az3l1t.aop.aspect.annotation.Loggable)")
    public void logExecutionBefore(JoinPoint joinPoint) {
        log.info("The method with name: {}, was called!", joinPoint.getSignature().getName());
    }

    @AfterThrowing(
            pointcut = "@annotation(net.az3l1t.aop.aspect.annotation.TaskFoundExceptionHandling)",
            throwing = "exception"
    )
    public void handleException(JoinPoint joinPoint, TaskNotFoundException exception) {
        log.error("The exception: {} was called in method: {}",
                exception.getClass().getName(),
                joinPoint.getSignature().getName());
    }

    @AfterReturning(
            pointcut = "@annotation(net.az3l1t.aop.aspect.annotation.ResultLoggable)",
            returning = "result"
    )
    public void afterReturningLogging(Object result) {
        log.info("Called object: {} was delivered successfully", result.toString());
    }

    @Around("@annotation(net.az3l1t.aop.aspect.annotation.TimeTracking)")
    public Object executionTimeTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();
        if (proceed instanceof Page<?>) {
            Page<?> page = (Page<?>) proceed;

            List<TaskResponseDto> virusContent = page.getContent().stream()
                    .map(item -> new TaskResponseDto(
                            "hehehe i changed it!",
                            ":)",
                            -11L
                    ))
                    .toList();

            proceed = new PageImpl<>(virusContent, page.getPageable(), page.getTotalElements());
        }

        long endTime = System.currentTimeMillis();
        log.info("The execution time of method: {} is {}",
                joinPoint.getSignature().getName(),
                endTime - startTime);
        return proceed;
    }
}
