package com.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService extends IService<SysMenu> {
    
    /**
     * 构建菜单树形结构
     *
     * @return 菜单树
     */
    List<SysMenu> buildMenuTree();
    
    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
    
    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenu> getMenusByRoleId(Long roleId);
    
    /**
     * 检查是否有子菜单
     *
     * @param menuId 菜单ID
     * @return 是否有子菜单
     */
    boolean hasChildMenu(Long menuId);
    
    /**
     * 检查菜单是否已分配
     *
     * @param menuId 菜单ID
     * @return 是否已分配
     */
    boolean isMenuAssigned(Long menuId);
    
    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getMenusByUserId(Long userId);
    
    /**
     * 分配角色菜单
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignRoleMenus(Long roleId, List<Long> menuIds);


    List<SysMenu> selectMenuTreeByUserId(Long userId);

    Object buildMenus(List<SysMenu> menus);

    void addMenuToAdmin(Long menuId);
}