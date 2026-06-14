package com.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<SysRole> {
    
    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);
    
    /**
     * 根据角色ID查询角色信息，包含菜单信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    SysRole getRoleWithMenus(Long roleId);
    
    /**
     * 分配角色菜单
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignRoleMenus(Long roleId, List<Long> menuIds);
    
    /**
     * 检查角色标识是否唯一
     *
     * @param roleKey 角色标识
     * @return true 如果唯一，false 如果已存在
     */
    boolean checkRoleKeyUnique(String roleKey);
    
    /**
     * 检查角色标识是否唯一（排除指定ID）
     *
     * @param roleKey 角色标识
     * @param roleId 角色ID
     * @return true 如果唯一，false 如果已存在
     */
    boolean checkRoleKeyUnique(String roleKey, Long roleId);
    
    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean createRole(SysRole role);
    
    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(SysRole role);
    
    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long roleId);
}