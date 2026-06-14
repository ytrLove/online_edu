package com.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.entity.SysMenu;
import com.user.mapper.MenuMapper;
import com.user.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, SysMenu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<SysMenu> buildMenuTree() {
        // 查询所有菜单
        List<SysMenu> menus = this.list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum));

        // 构建树形结构
        return buildMenuTree(menus, 0L);
    }

    /**
     * 递归构建菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    menu.setChildren(buildMenuTree(menus, menu.getMenuId()));
                    return menu;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public boolean hasChildMenu(Long menuId) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, menuId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean isMenuAssigned(Long menuId) {
        return menuMapper.countRoleMenuByMenuId(menuId) > 0;
    }

    @Override
    public List<SysMenu> getMenusByUserId(Long userId) {
        // 超级管理员显示所有菜单
        if (userId == 1L) {
            List<SysMenu> menus = this.list(new LambdaQueryWrapper<SysMenu>()
                  .eq(SysMenu::getStatus, 1)
                   .ne(SysMenu::getMenuType, "F") // 不显示按钮
                  .orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum));
            return buildMenuTree(menus, 0L);
        }
        
        // 普通用户显示有权限的菜单
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    @Override
    @Transactional
    public boolean assignRoleMenus(Long roleId, List<Long> menuIds) {
        // 先删除角色原有菜单
        menuMapper.deleteRoleMenus(roleId);
        // 分配新菜单
        if (menuIds != null && !menuIds.isEmpty()) {
            menuMapper.insertRoleMenus(roleId, menuIds);
        }
        
        return true;
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Object buildMenus(List<SysMenu> menus) {
        return null;
    }

    @Override
    public void addMenuToAdmin(Long menuId) {
        // 添加菜单到管理员角色
        menuMapper.insertRoleMenus(1L, List.of(menuId));
    }

    @Override
    public List<SysMenu> getMenusByRoleId(Long roleId) {
        // 超级管理员角色(roleId=1)拥有所有菜单权限
        if (roleId == 1L) {
            // 获取所有菜单
            return this.list(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum));
        } else {
            // 普通角色只获取已分配的菜单，不添加父菜单
            List<SysMenu> roleMenus = menuMapper.selectMenusByRoleId(roleId);
            
            // 如果菜单列表为空，直接返回空列表
            if (roleMenus == null || roleMenus.isEmpty()) {
                return new ArrayList<>();
            }

            return roleMenus;
        }
    }

}