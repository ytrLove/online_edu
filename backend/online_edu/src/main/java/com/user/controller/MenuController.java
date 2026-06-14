package com.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.core.domain.AjaxResult;
import com.user.model.dto.request.MenuCreateRequest;
import com.user.model.dto.request.MenuQueryRequest;
import com.user.model.dto.request.MenuUpdateRequest;
import com.user.model.dto.response.MenuInfoResponse;
import com.user.model.dto.response.MenuListResponse;
import com.user.model.entity.SysMenu;
import com.user.model.entity.SysUser;
import com.user.mapper.MenuConverter;
import com.user.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    
    // 使用静态INSTANCE而不是注入Bean
    private final MenuConverter menuConverter = MenuConverter.INSTANCE;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public AjaxResult list(MenuQueryRequest queryRequest) {
        try {
        // 构建查询条件
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(queryRequest.getMenuName() != null, SysMenu::getMenuName, queryRequest.getMenuName());
            queryWrapper.eq(queryRequest.getStatus() != null, SysMenu::getStatus, queryRequest.getStatus());
        queryWrapper.eq(SysMenu::getDelFlag, 0); // 只查询未删除的菜单
        queryWrapper.orderByAsc(SysMenu::getOrderNum); // 按排序字段升序排列

        // 直接查询所有符合条件的菜单
        List<SysMenu> menuList = menuService.list(queryWrapper);

        // 构建返回结果
            MenuListResponse response = new MenuListResponse();
            response.setTotal((long) menuList.size());
            response.setRows(safeConvertToMenuInfoResponses(menuList));

            return AjaxResult.success(response);
        } catch (Exception e) {
            return AjaxResult.fail("获取菜单列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public AjaxResult getInfo(@PathVariable Long menuId) {
        try {
        SysMenu menu = menuService.getById(menuId);
        if (menu != null) {
                MenuInfoResponse menuResponse = safeConvertToMenuInfoResponse(menu);
                return AjaxResult.success(menuResponse);
        }
        return AjaxResult.fail("未找到该菜单");
        } catch (Exception e) {
            return AjaxResult.fail("获取菜单详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取菜单树形结构
     */
    @GetMapping("/treeselect")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public AjaxResult treeselect() {
        try {
        List<SysMenu> menus = menuService.buildMenuTree();
            return AjaxResult.success(safeConvertToMenuInfoResponses(menus));
        } catch (Exception e) {
            return AjaxResult.fail("获取菜单树结构失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色菜单树形结构
     */
    @GetMapping("/roleMenuTreeSelect/{roleId}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public AjaxResult roleMenuTreeselect(@PathVariable Long roleId) {
        try {
            // 获取该角色已分配的菜单，以平铺结构返回
            List<SysMenu> roleMenus = menuService.getMenusByRoleId(roleId);
            
            // 不需要特殊处理为树形结构，直接转换为响应对象列表返回
            List<MenuInfoResponse> menuResponses = new ArrayList<>();
            for (SysMenu menu : roleMenus) {
                MenuInfoResponse menuResponse = safeConvertToMenuInfoResponse(menu);
                // 确保不包含children属性，防止前端误解为树形结构
                menuResponse.setChildren(null);
                menuResponses.add(menuResponse);
            }
            
            return AjaxResult.success(menuResponses);
        } catch (Exception e) {
            return AjaxResult.fail("获取角色菜单列表失败：" + e.getMessage());
        }
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public AjaxResult add(@RequestBody MenuCreateRequest request) {
        SysMenu menu = menuConverter.toEntity(request);
        menu.setDelFlag(0);
        if (menuService.save(menu)) {
            // 管理员用户默认添加这个菜单
            menuService.addMenuToAdmin(menu.getMenuId());
            return AjaxResult.success("新增菜单成功");
        }
        return AjaxResult.fail("新增菜单失败");
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public AjaxResult edit(@RequestBody MenuUpdateRequest request) {
        // 不能将自己设为父菜单
        if (request.getMenuId().equals(request.getParentId())) {
            return AjaxResult.fail("修改菜单失败，不能选择自己作为父菜单");
        }
        
        SysMenu menu = menuConverter.toEntity(request);
        if (menuService.updateById(menu)) {
            return AjaxResult.success("修改菜单成功");
        }
        return AjaxResult.fail("修改菜单失败");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    public AjaxResult remove(@PathVariable Long menuId) {
        // 检查是否有子菜单
        if (menuService.hasChildMenu(menuId)) {
            return AjaxResult.fail("存在子菜单，不允许删除");
        }
        
        // 检查菜单是否已分配
        if (menuService.isMenuAssigned(menuId)) {
            return AjaxResult.fail("菜单已分配，不允许删除");
        }
        
        if (menuService.removeById(menuId)) {
            return AjaxResult.success("删除菜单成功");
        }
        return AjaxResult.fail("删除菜单失败");
    }

    /**
     * 获取菜单树形结构
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public AjaxResult tree() {
        try {
        List<SysMenu> menuTree = menuService.buildMenuTree();
            return AjaxResult.success(safeConvertToMenuInfoResponses(menuTree));
        } catch (Exception e) {
            return AjaxResult.fail("获取菜单树结构失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户菜单
     */
    @GetMapping("/user/tree")
    public AjaxResult getUserMenu() {
        try {
        // 从SecurityContext中获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null || 
                !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
                return AjaxResult.fail("用户未登录或登录已过期");
            }
            
        SysUser user = (SysUser) authentication.getPrincipal();
        Long userId = user.getUserId();
        
        List<SysMenu> menus = menuService.getMenusByUserId(userId);
            return AjaxResult.success(safeConvertToMenuInfoResponses(menus));
        } catch (Exception e) {
            return AjaxResult.fail("获取菜单失败：" + e.getMessage());
        }
    }

    /**
     * 获取路由信息
     */
    @GetMapping("/routes")
    public AjaxResult getRoutes() {
        try {
        // 从SecurityContext中获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null || 
                !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
                return AjaxResult.fail("用户未登录或登录已过期");
            }
            
        SysUser user = (SysUser) authentication.getPrincipal();
        Long userId = user.getUserId();
        
        // 对于路由，我们也使用用户菜单，前端可以根据菜单类型进行处理
        List<SysMenu> menus = menuService.getMenusByUserId(userId);
            return AjaxResult.success(safeConvertToMenuInfoResponses(menus));
        } catch (Exception e) {
            return AjaxResult.fail("获取路由失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色菜单
     */
    @PutMapping("/role")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public AjaxResult updateRoleMenu(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        List<Long> menuIds = (List<Long>) params.get("menuIds");
        
        if (menuService.assignRoleMenus(roleId, menuIds)) {
            return AjaxResult.success("更新角色菜单成功");
        }
        return AjaxResult.fail("更新角色菜单失败");
    }

    /**
     * 辅助方法：安全地将实体列表转换为DTO列表
     */
    private List<MenuInfoResponse> safeConvertToMenuInfoResponses(List<SysMenu> menus) {
        if (menus == null) {
            return new ArrayList<>();
        }
        
        try {
            return menuConverter.toMenuInfoResponseList(menus);
        } catch (Exception e) {
            // 如果转换失败，手动进行简单转换
            List<MenuInfoResponse> menuResponses = new ArrayList<>();
            for (SysMenu menu : menus) {
                menuResponses.add(safeConvertToMenuInfoResponse(menu));
            }
            return menuResponses;
        }
    }
    
    /**
     * 辅助方法：安全地将单个实体转换为DTO
     */
    private MenuInfoResponse safeConvertToMenuInfoResponse(SysMenu menu) {
        if (menu == null) {
            return null;
        }
        
        try {
            return menuConverter.toMenuInfoResponse(menu);
        } catch (Exception e) {
            // 如果转换失败，手动进行简单转换
            MenuInfoResponse response = new MenuInfoResponse();
            response.setMenuId(menu.getMenuId());
            response.setMenuName(menu.getMenuName());
            response.setParentId(menu.getParentId());
            response.setPath(menu.getPath());
            response.setComponent(menu.getComponent());
            response.setIcon(menu.getIcon());
            response.setMenuType(menu.getMenuType());
            response.setOrderNum(menu.getOrderNum());
            response.setPerms(menu.getPerms());
            response.setStatus(menu.getStatus());
            response.setCreateTime(menu.getCreateTime());
            response.setUpdateTime(menu.getUpdateTime());
            response.setRemark(menu.getRemark());
            response.setIsButton(menu.getIsButton());
            
            // 递归处理子菜单
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                response.setChildren(safeConvertToMenuInfoResponses(menu.getChildren()));
            }
            
            return response;
        }
    }
}