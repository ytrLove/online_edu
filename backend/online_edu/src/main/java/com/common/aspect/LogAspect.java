package com.common.aspect;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.model.entity.OperationLog;
import com.user.model.entity.SysUser;
import com.user.service.LogService;
import com.user.service.LoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogService logService;
    private final ObjectMapper objectMapper;
    private final LoginLogService loginLogService;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void logPointCut() {}

    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(JoinPoint joinPoint, Exception e, Object jsonResult) {
        try {

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;

            HttpServletRequest request = attributes.getRequest();

            OperationLog operLog = new OperationLog();
            operLog.setStatus(e == null ? 0 : 1);
            operLog.setErrorMsg(e != null ? e.getMessage() : "");

            // 设置请求方法、请求URL等信息
            String methods = joinPoint.getSignature().getName();
            if ("getRoutes".equals(methods) || "getUserMenu".equals(methods) || "getLoginLogs".equals(methods) || "getUserInfo".equals(methods) || "getModules".equals(methods) || "getOperationLogs".equals(methods)){
                return;
            }
            operLog.setMethod(methods);
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperUrl(request.getRequestURI());
            // 获取操作者ip
//            String remoteUser = request.getRemoteUser();
            operLog.setOperIp(request.getRemoteAddr());
            operLog.setOperLocation(loginLogService.resolveIpLocation(request.getRemoteAddr()));
            operLog.setCreatedAt(LocalDateTime.now());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SysUser user = (SysUser) authentication.getPrincipal();

            String method = request.getMethod();
            if ("GET".equals(method)) {
                operLog.setOperatorType("查询操作");
            } else if ("POST".equals(method)) {
                operLog.setOperatorType("新增操作");
            } else if ("PUT".equals(method)) {
                operLog.setOperatorType("更新操作");
            } else if ("DELETE".equals(method)) {
                operLog.setOperatorType("删除操作");
            } else {
                operLog.setOperatorType("未知操作");
            }

            if (user == null) {

                operLog.setOperName("未知操作者");
            }else{
                operLog.setOperName(user.getUsername());
            }

            // 设置请求参数
            if (joinPoint.getArgs().length > 0) {
                try {
                    String params = objectMapper.writeValueAsString(joinPoint.getArgs());
                    operLog.setOperParam(params);
                } catch (Exception ex) {
                    log.error("记录操作日志异常：{}", ex.getMessage());
                }
            }

            // 设置返回结果
            if (jsonResult != null) {
                operLog.setJsonResult(null);
            }

            // 保存日志
            logService.recordOperLog(operLog);
        } catch (Exception exp) {
            log.error("记录操作日志异常：{}", exp.getMessage());
        }
    }
}