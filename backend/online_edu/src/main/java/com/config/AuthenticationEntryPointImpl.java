package com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证失败处理类
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEntryPointImpl.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("认证失败: {} - URI: {}", authException.getMessage(), request.getRequestURI());
        logger.debug("认证失败请求详情 - 方法: {}, 查询参数: {}, 请求头: {}", 
                    request.getMethod(), 
                    request.getQueryString(), 
                    request.getHeader("Authorization"));
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
        
        logger.debug("已发送认证失败响应: {}", body);
    }
}