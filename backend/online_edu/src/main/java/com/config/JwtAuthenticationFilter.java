package com.config;

import com.user.model.entity.SysUser;
import com.user.service.UserService;
import com.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final UserService userService;

    // 使用构造函数注入
    public JwtAuthenticationFilter(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 记录请求信息
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            logger.debug("收到请求: {} {}", method, requestURI);
            
            String jwt = parseJwt(request);
            if (jwt != null) {
                // 验证JWT令牌
                boolean isValid = jwtUtils.validateJwtToken(jwt);

                logger.debug("JWT令牌验证结果: {}", isValid);
                
                if (isValid) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    logger.debug("从JWT解析到的用户名: {}", username);


                    // 通过用户名加载用户信息及权限
                    UserDetails userDetails = userService.getUserByUsername(username);
                    if (userDetails != null) {
                        // 输出详细的权限信息
                        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                        if (authorities != null && !authorities.isEmpty()) {
                            String authoritiesStr = authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.joining(", "));
                            logger.debug("用户 {} 的权限列表: {}", username, authoritiesStr);
                            if (userDetails instanceof SysUser) {
                                SysUser sysUser = (SysUser) userDetails;
                                logger.debug("用户 {} 的角色数量: {}", username, sysUser.getRoles() != null ? sysUser.getRoles().size() : 0);
                                if (sysUser.getRoles() != null) {
                                    sysUser.getRoles().forEach(role -> {
                                        logger.debug("角色 {} 拥有权限数量: {}", role.getRoleName(), 
                                                role.getPermissions() != null ? role.getPermissions().size() : 0);
                                    });
                                }
                            }
                            
                            // 检查请求的URI是否匹配/api/edu/**
                            if (requestURI.startsWith("/api/edu/")) {
                                boolean hasEduPermission = authorities.stream()
                                        .anyMatch(a -> a.getAuthority().startsWith("edu:"));
                                logger.debug("请求 {} 需要edu:前缀的权限，用户是否拥有: {}", requestURI, hasEduPermission);
                            }
                        } else {
                            logger.warn("用户 {} 没有任何权限", username);
                        }
                        
                        // 设置认证信息
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("已设置认证信息到SecurityContext");
                    } else {
                        logger.warn("找不到用户: {}", username);
                    }
                } else {
                    logger.warn("无效的JWT令牌");
                }
            } else {
                logger.debug("请求中未包含JWT令牌: {}", requestURI);
            }
        } catch (Exception e) {
            logger.error("认证过程中发生错误: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            logger.debug("已解析Bearer Token，长度: {}", token.length());
            return token;
        }

        return null;
    }
}