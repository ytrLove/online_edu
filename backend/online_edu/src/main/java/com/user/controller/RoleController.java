package com.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.user.model.dto.request.AssignMenusRequest;
import com.user.model.dto.request.RoleCreateRequest;
import com.user.model.dto.request.RoleQueryRequest;
import com.user.model.dto.request.RoleUpdateRequest;
import com.user.model.dto.response.MenuInfoResponse;
import com.user.model.dto.response.RoleDetailResponse;
import com.user.model.dto.response.RoleListResponse;
import com.user.model.entity.SysMenu;
import com.user.model.entity.SysRole;
import com.user.mapper.MenuConverter;
import com.user.mapper.RoleConverter;
import com.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    
    // 使用静态INSTANCE而不是注入Bean
    private final RoleConverter roleConverter = RoleConverter.INSTANCE;
    private final MenuConverter menuConverter = MenuConverter.INSTANCE;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public AjaxResult list(RoleQueryRequest queryRequest) {
        // 构建查询条件
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(queryRequest.getRoleName() != null && !queryRequest.getRoleName().trim().isEmpty(), 
                SysRole::getRoleName, queryRequest.getRoleName());
        queryWrapper.like(queryRequest.getRoleKey() != null && !queryRequest.getRoleKey().trim().isEmpty(), 
                SysRole::getRoleKey, queryRequest.getRoleKey());
        queryWrapper.eq(queryRequest.getStatus() != null, SysRole::getStatus, queryRequest.getStatus());
        queryWrapper.orderByAsc(SysRole::getRoleSort); // 按排序升序

        // 分页查询
        Page<SysRole> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        Page<SysRole> rolePage = roleService.page(page, queryWrapper);

        // 加载角色菜单信息
        List<SysRole> roleList = rolePage.getRecords();
        for (SysRole role : roleList) {
            // 使用getRoleWithMenus方法加载角色的菜单信息
            SysRole roleWithMenus = roleService.getRoleWithMenus(role.getRoleId());
            if (roleWithMenus != null && roleWithMenus.getMenus() != null) {
                role.setMenus(roleWithMenus.getMenus());
            }
        }

        // 构建返回结果
        RoleListResponse response = new RoleListResponse();
        response.setTotal(rolePage.getTotal());
        
        // 手动转换角色列表
        List<RoleDetailResponse> roleResponses = new ArrayList<>();
        for (SysRole role : roleList) {
            RoleDetailResponse roleResponse = safeConvertToRoleDetailResponse(role);
            roleResponses.add(roleResponse);
        }
        response.setRows(roleResponses);

        return AjaxResult.success(response);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public AjaxResult getInfo(@PathVariable Long roleId) {
        try {
            SysRole role = roleService.getRoleWithMenus(roleId);
            if (role != null) {
                RoleDetailResponse roleResponse = new RoleDetailResponse();
                roleResponse.setRoleId(role.getRoleId());
                roleResponse.setRoleName(role.getRoleName());
                roleResponse.setRoleKey(role.getRoleKey());
                roleResponse.setRoleSort(role.getRoleSort());
                roleResponse.setStatus(role.getStatus());
                roleResponse.setCreateTime(role.getCreateTime());
                roleResponse.setUpdateTime(role.getUpdateTime());
                roleResponse.setRemark(role.getRemark());
                
                // 不再构建树形结构，直接返回所有权限的平铺列表
                List<MenuInfoResponse> menuResponses = new ArrayList<>();
                if (role.getMenus() != null) {
                    for (SysMenu menu : role.getMenus()) {
                        MenuInfoResponse menuResponse = new MenuInfoResponse();
                        menuResponse.setMenuId(menu.getMenuId());
                        menuResponse.setMenuName(menu.getMenuName());
                        menuResponse.setParentId(menu.getParentId());
                        menuResponse.setPath(menu.getPath());
                        menuResponse.setComponent(menu.getComponent());
                        menuResponse.setPerms(menu.getPerms());
                        menuResponse.setIcon(menu.getIcon());
                        menuResponse.setMenuType(menu.getMenuType());
                        menuResponse.setOrderNum(menu.getOrderNum());
                        menuResponse.setStatus(menu.getStatus());
                        menuResponse.setIsButton(menu.getIsButton());
                        
                        menuResponses.add(menuResponse);
                        
                        // 如果菜单有子菜单，将子菜单也直接添加到平铺的列表中
                        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                            for (SysMenu childMenu : menu.getChildren()) {
                                MenuInfoResponse childResponse = new MenuInfoResponse();
                                childResponse.setMenuId(childMenu.getMenuId());
                                childResponse.setMenuName(childMenu.getMenuName());
                                childResponse.setParentId(childMenu.getParentId());
                                childResponse.setPath(childMenu.getPath());
                                childResponse.setComponent(childMenu.getComponent());
                                childResponse.setPerms(childMenu.getPerms());
                                childResponse.setIcon(childMenu.getIcon());
                                childResponse.setMenuType(childMenu.getMenuType());
                                childResponse.setOrderNum(childMenu.getOrderNum());
                                childResponse.setStatus(childMenu.getStatus());
                                childResponse.setIsButton(childMenu.getIsButton());
                                
                                menuResponses.add(childResponse);
                                
                                // 如果还有更深层次的子菜单，也递归添加
                                addChildrenMenusFlat(childMenu, menuResponses);
                            }
                        }
                    }
                }
                roleResponse.setMenus(menuResponses);
                
                return AjaxResult.success(roleResponse);
            }
            return AjaxResult.fail("未找到该角色");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.fail("获取角色信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 递归添加子菜单到平铺列表
     */
    private void addChildrenMenusFlat(SysMenu menu, List<MenuInfoResponse> menuResponses) {
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            for (SysMenu childMenu : menu.getChildren()) {
                MenuInfoResponse childResponse = new MenuInfoResponse();
                childResponse.setMenuId(childMenu.getMenuId());
                childResponse.setMenuName(childMenu.getMenuName());
                childResponse.setParentId(childMenu.getParentId());
                childResponse.setPath(childMenu.getPath());
                childResponse.setComponent(childMenu.getComponent());
                childResponse.setPerms(childMenu.getPerms());
                childResponse.setIcon(childMenu.getIcon());
                childResponse.setMenuType(childMenu.getMenuType());
                childResponse.setOrderNum(childMenu.getOrderNum());
                childResponse.setStatus(childMenu.getStatus());
                childResponse.setIsButton(childMenu.getIsButton());
                
                menuResponses.add(childResponse);
                
                // 递归处理更深层次的子菜单
                addChildrenMenusFlat(childMenu, menuResponses);
            }
        }
    }

    /**
     * 辅助方法：安全地将单个实体转换为DTO
     */
    private RoleDetailResponse safeConvertToRoleDetailResponse(SysRole role) {
        if (role == null) {
            return null;
        }
        
        try {
            return roleConverter.toRoleDetailResponse(role);
        } catch (Exception e) {
            // 如果转换失败，手动进行简单转换
            RoleDetailResponse response = new RoleDetailResponse();
            response.setRoleId(role.getRoleId());
            response.setRoleName(role.getRoleName());
            response.setRoleKey(role.getRoleKey());
            response.setRoleSort(role.getRoleSort());
            response.setStatus(role.getStatus());
            
            // 手动转换菜单列表
            List<MenuInfoResponse> menuResponses = new ArrayList<>();
            if (role.getMenus() != null) {
                for (SysMenu menu : role.getMenus()) {
                    MenuInfoResponse menuResponse = new MenuInfoResponse();
                    menuResponse.setMenuId(menu.getMenuId());
                    menuResponse.setMenuName(menu.getMenuName());
                    menuResponse.setParentId(menu.getParentId());
                    menuResponse.setPath(menu.getPath());
                    menuResponse.setComponent(menu.getComponent());
                    menuResponse.setPerms(menu.getPerms());
                    menuResponse.setIcon(menu.getIcon());
                    menuResponse.setMenuType(menu.getMenuType());
                    menuResponse.setOrderNum(menu.getOrderNum());
                    menuResponse.setStatus(menu.getStatus());
                    menuResponse.setIsButton(menu.getIsButton());

                    
                    // 递归处理子菜单
                    if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                        List<MenuInfoResponse> childrenResponses = new ArrayList<>();
                        for (SysMenu childMenu : menu.getChildren()) {
                            // 简化处理，仅转换第一级子菜单
                            MenuInfoResponse childResponse = new MenuInfoResponse();
                            childResponse.setMenuId(childMenu.getMenuId());
                            childResponse.setMenuName(childMenu.getMenuName());
                            childResponse.setParentId(childMenu.getParentId());
                            childResponse.setPath(childMenu.getPath());
                            childResponse.setComponent(childMenu.getComponent());
                            childResponse.setPerms(childMenu.getPerms());
                            childResponse.setIcon(childMenu.getIcon());
                            childResponse.setMenuType(childMenu.getMenuType());
                            childResponse.setOrderNum(childMenu.getOrderNum());
                            childResponse.setStatus(childMenu.getStatus());
                            childResponse.setIsButton(childMenu.getIsButton());

                            
                            childrenResponses.add(childResponse);
                        }
                        menuResponse.setChildren(childrenResponses);
                    }
                    
                    menuResponses.add(menuResponse);
                }
            }
            response.setMenus(menuResponses);
            
            response.setCreateTime(role.getCreateTime());
            response.setUpdateTime(role.getUpdateTime());
            response.setRemark(role.getRemark());
            return response;
        }
    }

    /**
     * 新增角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public AjaxResult add(@RequestBody RoleCreateRequest request) {
        // 检查角色标识是否已存在
        if (!roleService.checkRoleKeyUnique(request.getRoleKey())) {
            return AjaxResult.fail("角色标识已存在");
        }

        SysRole role = safeConvertToEntity(request);
        // 处理菜单分配
        List<Long> menuIds = request.getMenuIds();

        if (roleService.createRole(role)) {
            // 如果有菜单ID列表，分配菜单
            if (menuIds != null && !menuIds.isEmpty()) {
                roleService.assignRoleMenus(role.getRoleId(), menuIds);
            }
            return AjaxResult.success("新增角色成功");
        }
        return AjaxResult.fail("新增角色失败");
    }
    
    /**
     * 辅助方法：安全地将RoleCreateRequest转换为SysRole实体
     */
    private SysRole safeConvertToEntity(RoleCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        try {
            return roleConverter.toEntity(request);
        } catch (Exception e) {
            // 如果转换失败，手动进行简单转换
            SysRole role = new SysRole();
            role.setRoleName(request.getRoleName());
            role.setRoleKey(request.getRoleKey());
            role.setRoleSort(request.getRoleSort());
            role.setStatus(request.getStatus());
            role.setRemark(request.getRemark());
            role.setDelFlag(0); // 默认未删除
            return role;
        }
    }

    /**
     * 修改角色
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public AjaxResult edit(@RequestBody RoleUpdateRequest request) {
        // 检查角色标识是否已存在（排除自身）
        if (!roleService.checkRoleKeyUnique(request.getRoleKey(), request.getRoleId())) {
            return AjaxResult.fail("角色标识已存在");
        }

        SysRole role = safeConvertToEntity(request);
        // 处理菜单分配
        List<Long> menuIds = request.getMenuIds();

        if (roleService.updateRole(role)) {
            // 如果有菜单ID列表，重新分配菜单
            if (menuIds != null && !menuIds.isEmpty()) {
                roleService.assignRoleMenus(role.getRoleId(), menuIds);
            }
            return AjaxResult.success("修改角色成功");
        }
        return AjaxResult.fail("修改角色失败");
    }
    
    /**
     * 辅助方法：安全地将RoleUpdateRequest转换为SysRole实体
     */
    private SysRole safeConvertToEntity(RoleUpdateRequest request) {
        if (request == null) {
            return null;
        }
        
        try {
            return roleConverter.toEntity(request);
        } catch (Exception e) {
            // 如果转换失败，手动进行简单转换
            SysRole role = new SysRole();
            role.setRoleId(request.getRoleId());
            role.setRoleName(request.getRoleName());
            role.setRoleKey(request.getRoleKey());
            role.setRoleSort(request.getRoleSort());
            role.setStatus(request.getStatus());
            role.setRemark(request.getRemark());
            return role;
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('system:role:remove')")
    public AjaxResult remove(@PathVariable Long roleId) {
        // 不允许删除管理员角色
        if (roleId == 1L) {
            return AjaxResult.fail("不允许删除管理员角色");
        }

        if (roleService.deleteRole(roleId)) {
            return AjaxResult.success("删除角色成功");
        }
        return AjaxResult.fail("删除角色失败");
    }

    /**
     * 分配角色菜单
     */
    @PutMapping("/assignMenus")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public AjaxResult assignMenus(@RequestBody AssignMenusRequest request) {
        if (roleService.assignRoleMenus(request.getRoleId(), request.getMenuIds())) {
            return AjaxResult.success("分配菜单成功");
        }
        return AjaxResult.fail("分配菜单失败");
    }

    /**
     * 获取所有角色列表（不分页）
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role:query')")
    public AjaxResult all() {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getStatus, 1); // 只查询正常状态的角色
        queryWrapper.orderByAsc(SysRole::getRoleSort); // 按排序升序

        List<SysRole> roles = roleService.list(queryWrapper);
        
        // 手动转换角色列表
        List<RoleDetailResponse> roleResponses = new ArrayList<>();
        for (SysRole role : roles) {
            RoleDetailResponse roleResponse = safeConvertToRoleDetailResponse(role);
            roleResponses.add(roleResponse);
        }
        return AjaxResult.success(roleResponses);
    }
}