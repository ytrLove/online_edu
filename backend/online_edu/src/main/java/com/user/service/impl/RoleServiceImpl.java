package com.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.entity.SysRole;
import com.user.mapper.RoleMapper;
import com.user.service.MenuService;
import com.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private MenuService menuService;
    
    @Override
    @Transactional(readOnly = true)
    public List<SysRole> getRolesByUserId(Long userId) {
        List<SysRole> roles = roleMapper.selectRolesByUserIdWithMenus(userId);
        return roles;
    }
    
    @Override
    public boolean checkRoleKeyUnique(String roleKey) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleKey, roleKey);
        return this.count(queryWrapper) == 0;
    }
    
    @Override
    public boolean checkRoleKeyUnique(String roleKey, Long roleId) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleKey, roleKey)
                .ne(SysRole::getRoleId, roleId);
        return this.count(queryWrapper) == 0;
    }
    
    @Override
    @Transactional
    public boolean createRole(SysRole role) {
        role.setCreateTime(LocalDateTime.now());
        role.setStatus(1);
        role.setDelFlag(0);
        return super.save(role);
    }
    
    @Override
    @Transactional
    public boolean updateRole(SysRole role) {
        role.setUpdateTime(LocalDateTime.now());
        return super.updateById(role);
    }
    
    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        // 1. 删除用户-角色关联
        roleMapper.deleteUserRolesByRoleId(roleId);
        
        // 2. 删除角色-菜单关联
        roleMapper.deleteRoleMenusByRoleId(roleId);
        
        // 3. 物理删除角色记录
        return super.removeById(roleId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SysRole getRoleWithMenus(Long roleId) {
        SysRole role = this.getById(roleId);
        if (role != null) {
            role.setMenus(menuService.getMenusByRoleId(roleId));
        }
        return role;
    }
    
    @Override
    @Transactional
    public boolean assignRoleMenus(Long roleId, List<Long> menuIds) {
        return menuService.assignRoleMenus(roleId, menuIds);
    }
}