package com.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.user.model.dto.request.AssignRolesRequest;
import com.user.model.dto.request.ResetPasswordRequest;
import com.user.model.dto.request.UserCreateRequest;
import com.user.model.dto.request.UserQueryRequest;
import com.user.model.dto.request.UserUpdateRequest;
import com.user.model.dto.response.UserInfoResponse;
import com.user.model.dto.response.UserListResponse;
import com.user.model.entity.SysUser;
import com.user.mapper.UserConverter;
import com.user.service.RoleService;
import com.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserConverter userConverter;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    public AjaxResult<UserListResponse> list(UserQueryRequest queryRequest) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(queryRequest.getUsername() != null, SysUser::getUsername, queryRequest.getUsername());
        queryWrapper.eq(queryRequest.getStatus() != null, SysUser::getStatus, queryRequest.getStatus());
        queryWrapper.eq(SysUser::getDelFlag, 0); // 只查询未删除的用户

        // 分页查询
        Page<SysUser> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        Page<SysUser> userPage = userService.page(page, queryWrapper);

        // 加载用户角色信息
        List<SysUser> userList = userPage.getRecords();
        for (SysUser user : userList) {
            user.setRoles(roleService.getRolesByUserId(user.getUserId()));
            user.setPassword(null); // 清空密码
        }

        // 构建返回结果
        UserListResponse response = new UserListResponse();
        response.setTotal(userPage.getTotal());
        response.setRows(userConverter.toResponseList(userList));

        return AjaxResult.success(response);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public AjaxResult<?> getInfo(@PathVariable Long userId) {
        SysUser user = userService.getById(userId);
        if (user != null) {
            // 查询用户角色
            user.setRoles(roleService.getRolesByUserId(userId));
            // 清空密码
            user.setPassword(null);
            
            // 转换为响应对象
            UserInfoResponse response = userConverter.toResponse(user);
            return AjaxResult.success(response);
        }
        return AjaxResult.fail("未找到该用户");
    }

    /**
     * 新增用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public AjaxResult<?> add(@Valid @RequestBody UserCreateRequest createRequest) {
        if (userService.getUserByUsername(createRequest.getUsername()) != null) {
            return AjaxResult.fail("用户名已存在");
        }

        // 转换为实体对象
        SysUser user = userConverter.toEntity(createRequest);
        
        // 注册用户并分配角色
        if (userService.registerUserWithRole(user, createRequest.getRoleIds())) {
            return AjaxResult.success("新增用户成功");
        }
        return AjaxResult.fail("新增用户失败");
    }

    /**
     * 修改用户
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public AjaxResult<?> edit(@Valid @RequestBody UserUpdateRequest updateRequest) {
        // 转换为实体对象
        SysUser user = userConverter.toEntity(updateRequest);
        
        // 更新用户信息
        if (userService.updateUserInfo(user)) {
            // 如果有角色信息，单独更新角色
            if (updateRequest.getRoleIds() != null) {
                userService.assignUserRoles(user.getUserId(), updateRequest.getRoleIds());
            }
            return AjaxResult.success("修改用户成功");
        }
        return AjaxResult.fail("修改用户失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('system:user:remove')")
    public AjaxResult<?> remove(@PathVariable Long userId) {
        // 不允许删除管理员
        if (userId == 1L) {
            return AjaxResult.fail("不允许删除管理员");
        }

        if (userService.deleteUserCompletely(userId)) {
            return AjaxResult.success("删除用户成功");
        }
        return AjaxResult.fail("删除用户失败");
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public AjaxResult<?> resetPwd(@Valid @RequestBody ResetPasswordRequest resetPwdRequest) {
        if (userService.resetPassword(resetPwdRequest.getUserId(), resetPwdRequest.getPassword())) {
            return AjaxResult.success("重置密码成功");
        }
        return AjaxResult.fail("重置密码失败");
    }

    /**
     * 分配用户角色
     */
    @PutMapping("/assignRoles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public AjaxResult<?> assignRoles(@Valid @RequestBody AssignRolesRequest assignRolesRequest) {
        if (userService.assignUserRoles(assignRolesRequest.getUserId(), assignRolesRequest.getRoleIds())) {
            return AjaxResult.success("分配角色成功");
        }
        return AjaxResult.fail("分配角色失败");
    }
}