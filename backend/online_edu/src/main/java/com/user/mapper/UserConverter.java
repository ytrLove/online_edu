package com.user.mapper;

import com.user.model.dto.request.UserCreateRequest;
import com.user.model.dto.request.UserUpdateRequest;
import com.user.model.dto.response.MenuInfoResponse;
import com.user.model.dto.response.RoleInfoResponse;
import com.user.model.dto.response.UserInfoResponse;
import com.user.model.entity.SysMenu;
import com.user.model.entity.SysRole;
import com.user.model.entity.SysUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户实体类转换器
 * 用于Entity和DTO之间的转换
 */
@Component
public class UserConverter {

    /**
     * 将用户创建请求转换为用户实体
     *
     * @param request 用户创建请求
     * @return 用户实体
     */
    public SysUser toEntity(UserCreateRequest request) {
        if (request == null) {
            return null;
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        return user;
    }

    /**
     * 将用户更新请求转换为用户实体
     *
     * @param request 用户更新请求
     * @return 用户实体
     */
    public SysUser toEntity(UserUpdateRequest request) {
        if (request == null) {
            return null;
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        return user;
    }

    /**
     * 将用户实体转换为用户信息响应
     *
     * @param entity 用户实体
     * @return 用户信息响应
     */
    public UserInfoResponse toResponse(SysUser entity) {
        if (entity == null) {
            return null;
        }
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(entity, response);
        
        // 转换角色信息
        if (entity.getRoles() != null) {
            response.setRoles(entity.getRoles().stream()
                    .map(this::toRoleInfoResponse)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }

    /**
     * 将用户实体列表转换为用户信息响应列表
     *
     * @param entities 用户实体列表
     * @return 用户信息响应列表
     */
    public List<UserInfoResponse> toResponseList(List<SysUser> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * 将角色实体转换为角色信息响应
     *
     * @param role 角色实体
     * @return 角色信息响应
     */
    public RoleInfoResponse toRoleInfoResponse(SysRole role) {
        if (role == null) {
            return null;
        }
        RoleInfoResponse response = new RoleInfoResponse();
        BeanUtils.copyProperties(role, response);
        
        // 转换菜单信息
        if (role.getMenus() != null) {
            response.setMenus(role.getMenus().stream()
                    .map(this::toMenuInfoResponse)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }

    /**
     * 将菜单实体转换为菜单信息响应
     *
     * @param menu 菜单实体
     * @return 菜单信息响应
     */
    public MenuInfoResponse toMenuInfoResponse(SysMenu menu) {
        if (menu == null) {
            return null;
        }
        MenuInfoResponse response = new MenuInfoResponse();
        BeanUtils.copyProperties(menu, response);
        
        // 转换子菜单
        if (menu.getChildren() != null) {
            response.setChildren(menu.getChildren().stream()
                    .map(this::toMenuInfoResponse)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
} 