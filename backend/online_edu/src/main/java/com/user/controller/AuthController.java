package com.user.controller;

import com.common.core.domain.AjaxResult;
import com.user.model.entity.SysMenu;
import com.user.model.entity.SysRole;
import com.user.model.entity.SysUser;
import com.user.service.LoginLogService;
import com.user.service.MenuService;
import com.user.service.UserService;
import com.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final MenuService menuService;
    private final PasswordEncoder passwordEncoder;
    private final LoginLogService loginLogService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtils jwtUtils, 
                          MenuService menuService,
                          PasswordEncoder passwordEncoder,
                          LoginLogService loginLogService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.menuService = menuService;
        this.passwordEncoder = passwordEncoder;
        this.loginLogService = loginLogService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public AjaxResult login(@Validated @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            
            // 将认证信息存入SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成JWT令牌
            String jwt = jwtUtils.generateJwtToken(loginRequest.getUsername());

            // 获取用户信息
            SysUser userDetails = (SysUser) authentication.getPrincipal();
            
            // 获取用户权限列表
            Set<String> permissions = new HashSet<>();
            List<SysRole> roles = userDetails.getRoles();
            
            // 检查是否包含管理员角色
            boolean isAdmin = false;
            if (roles != null) {
                for (SysRole role : roles) {
                    if ("admin".equals(role.getRoleKey())) {
                        isAdmin = true;
                        break;
                    }
                }
                
                // 如果是管理员，添加通配符权限
                if (isAdmin) {
                    permissions.add("*:*:*");
                } else {
                    // 非管理员则获取正常的权限列表
                    for (SysRole role : roles) {
                        if (role.getMenus() != null) {
                            permissions.addAll(role.getMenus().stream()
                                .filter(menu -> menu.getPerms() != null && !menu.getPerms().isEmpty())
                                .map(SysMenu::getPerms)
                                .collect(Collectors.toSet()));
                        }
                    }
                }
            }
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", userDetails);
            response.put("roles", userDetails.getRoles().stream().map(SysRole::getRoleKey).collect(Collectors.toList()));
            response.put("permissions", permissions);
            loginLogService.recordLoginLog(request, loginRequest.getUsername(), true, "登录成功");
            return AjaxResult.success(response);
        } catch (BadCredentialsException e) {
            return AjaxResult.fail("用户名或密码错误");
        } catch (DisabledException e) {
            return AjaxResult.fail("账号已被禁用");
        } catch (Exception e) {
            return AjaxResult.fail("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public AjaxResult refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.getToken();
        
        // 验证令牌
        if (!jwtUtils.validateJwtToken(token)) {
            return AjaxResult.fail("无效的令牌");
        }
        
        // 获取用户名
        String username = jwtUtils.getUserNameFromJwtToken(token);
        
        // 验证用户是否存在且状态正常
        SysUser user = userService.getUserByUsername(username);
        if (user == null) {
            return AjaxResult.fail("用户不存在");
        }
        
        if (user.getStatus() != 1) {
            return AjaxResult.fail("用户已被禁用");
        }
        
        // 生成新令牌
        String newToken = jwtUtils.generateJwtToken(username);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("userId", user.getUserId());
        
        return AjaxResult.success(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public AjaxResult register(@Validated @RequestBody RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userService.getUserByUsername(registerRequest.getUsername()) != null) {
            return AjaxResult.fail("用户名已存在");
        }

        // 邮箱格式验证
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            return AjaxResult.fail("邮箱格式不正确");
        }
        
        // 手机号格式验证
        if (registerRequest.getPhone() != null && !registerRequest.getPhone().matches("^1[3-9]\\d{9}$")) {
            return AjaxResult.fail("手机号格式不正确");
        }

        // 密码复杂度检查
        if (registerRequest.getPassword().length() < 6) {
            return AjaxResult.fail("密码长度不能小于6位");
        }

        // 创建用户对象
        SysUser user = new SysUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setNickname(registerRequest.getNickname());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setStatus(1); // 默认启用

        // 注册用户
        boolean success = userService.registerUser(user);

        if (success) {
            return AjaxResult.success("注册成功");
        } else {
            return AjaxResult.fail("注册失败");
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userInfo")
    public AjaxResult getUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SysUser user = (SysUser) authentication.getPrincipal();
            Long userId = user.getUserId();
            
            // 获取完整的用户信息（包括角色和权限）
            SysUser userInfo = userService.getUserByUsername(user.getUsername());
            
            // 清除敏感信息
            userInfo.setPassword(null);
            
            // 获取权限列表
            Set<String> permissions = new HashSet<>();
            List<SysRole> roles = userInfo.getRoles();
            
            // 检查是否包含管理员角色
            boolean isAdmin = false;
            if (roles != null) {
                for (SysRole role : roles) {
                    if ("admin".equals(role.getRoleKey())) {
                        isAdmin = true;
                        break;
                    }
                }
                
                // 如果是管理员，添加通配符权限
                if (isAdmin) {
                    permissions.add("*:*:*");
                } else {
                    // 非管理员则获取正常的权限列表
                    for (SysRole role : roles) {
                        if (role.getMenus() != null) {
                            permissions.addAll(role.getMenus().stream()
                                .filter(menu -> menu.getPerms() != null && !menu.getPerms().isEmpty())
                                .map(SysMenu::getPerms)
                                .collect(Collectors.toSet()));
                        }
                    }
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("user", userInfo);
            result.put("roles", userInfo.getRoles().stream().map(SysRole::getRoleKey).collect(Collectors.toList()));
            result.put("permissions", permissions);
            
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.fail("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取路由信息
     */
    @GetMapping("/getRouters")
    public AjaxResult getRouters() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SysUser user = (SysUser) authentication.getPrincipal();
            Long userId = user.getUserId();
            
            // 获取用户菜单列表
            List<SysMenu> menus = menuService.getMenusByUserId(userId);
            
            return AjaxResult.success(menus);
        } catch (Exception e) {
            return AjaxResult.fail("获取路由信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public AjaxResult logout() {
        SecurityContextHolder.clearContext();
        return AjaxResult.success("退出成功");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/updateUser")
    public AjaxResult updateUser(@RequestBody SysUser userInfo) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SysUser currentUser = (SysUser) authentication.getPrincipal();
            Long userId = currentUser.getUserId();
            
            // 设置用户ID，确保只能修改自己的信息
            userInfo.setUserId(userId);
            
            // 验证邮箱格式
            if (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty() && 
                !userInfo.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                return AjaxResult.fail("邮箱格式不正确");
            }
            
            // 验证手机号格式
            if (userInfo.getPhone() != null && !userInfo.getPhone().isEmpty() && 
                !userInfo.getPhone().matches("^1[3-9]\\d{9}$")) {
                return AjaxResult.fail("手机号格式不正确");
            }
            
            // 确保不修改敏感字段
            userInfo.setUsername(null);  // 不允许修改用户名
            userInfo.setPassword(null);  // 不通过此接口修改密码
            userInfo.setStatus(null);    // 不允许修改状态
            
            // 调用服务更新用户信息
            boolean success = userService.updateUserInfo(userInfo);
            
            if (success) {
                return AjaxResult.success("更新用户信息成功");
            } else {
                return AjaxResult.fail("更新用户信息失败");
            }
        } catch (Exception e) {
            return AjaxResult.fail("更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/changePassword")
    public AjaxResult changePassword(@RequestBody Map<String, String> params) {
        try {
            // 获取参数
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            
            // 参数校验
            if (oldPassword == null || oldPassword.isEmpty()) {
                return AjaxResult.fail("旧密码不能为空");
            }
            
            if (newPassword == null || newPassword.isEmpty()) {
                return AjaxResult.fail("新密码不能为空");
            }
            
            // 密码复杂度检查
            if (newPassword.length() < 6) {
                return AjaxResult.fail("新密码长度不能小于6位");
            }
            
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SysUser currentUser = (SysUser) authentication.getPrincipal();
            
            // 获取完整的用户信息
            SysUser user = userService.getUserByUsername(currentUser.getUsername());
            
            // 验证旧密码（此处需要自行实现密码验证逻辑，或者通过认证管理器验证）
            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), oldPassword));
                
                // 验证成功，对新密码进行加密
                String encodedPassword = passwordEncoder.encode(newPassword);
                
                // 更新加密后的密码
                user.setPassword(encodedPassword);
                boolean success = userService.updateById(user);
                
                if (success) {
                    return AjaxResult.success("密码修改成功");
                } else {
                    return AjaxResult.fail("密码修改失败");
                }
            } catch (BadCredentialsException e) {
                return AjaxResult.fail("旧密码不正确");
            }
        } catch (Exception e) {
            return AjaxResult.fail("密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 登录请求
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 注册请求
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String nickname;
        private String email;
        private String phone;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
    
    /**
     * 刷新令牌请求
     */
    public static class RefreshTokenRequest {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}