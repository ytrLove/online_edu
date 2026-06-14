package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.model.entity.SysRole;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface RoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色列表，包含菜单信息
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserIdWithMenus(Long userId);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);
    
    /**
     * 根据角色ID查询角色信息，包含菜单信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    SysRole selectRoleWithMenus(Long roleId);

    /**
     * 删除角色权限关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRolePermissions(Long roleId);
    
    /**
     * 批量插入角色权限关联
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 影响行数
     */
    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 删除用户角色关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteUserRolesByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 删除角色菜单关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRoleMenusByRoleId(@Param("roleId") Long roleId);
}
