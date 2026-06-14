package com.common.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MyAnnotationAspect {
    private final ChatMemory chatMemory;

    @Pointcut("@annotation(com.common.core.zhujie.MyAnnotation)")
    public void logPointCut() {}

    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) throws NoSuchMethodException {
        handleLog(joinPoint, null, jsonResult);
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) throws NoSuchMethodException {
        handleLog(joinPoint, e, null);
    }

    protected Object handleLog(JoinPoint joinPoint, Exception e, Object jsonResult) throws NoSuchMethodException {
        if (joinPoint == null || chatMemory == null) {
            log.warn("Invalid arguments: joinPoint or chatMemory is null");
            return jsonResult;
        }

        String methodName = joinPoint.getSignature().getName();
        log.info("Method name: {}", methodName);
        try {
            Method targetMethod = chatMemory.getClass().getDeclaredMethod(methodName);
            targetMethod.setAccessible(true);

            Object result = targetMethod.invoke(chatMemory);
            log.info("Method {} invoked successfully, result: {}", methodName, result);
            return result;
        } catch (NoSuchMethodException ex) {
            log.error("Method {} not found in ChatMemory", methodName, ex);
            throw ex;
        } catch (IllegalAccessException ex) {
            log.error("Access denied when calling method {}", methodName, ex);
            throw new RuntimeException("Access denied for method: " + methodName, ex);
        } catch (InvocationTargetException ex) {
            log.error("Method {} invocation failed", methodName, ex.getTargetException());
            throw new RuntimeException("Invocation failed for method: " + methodName, ex.getTargetException());
        } catch (Exception ex) {
            log.error("Unexpected error when calling method {}", methodName, ex);
            throw new RuntimeException("Unexpected error when calling method: " + methodName, ex);
        }
    }
}
