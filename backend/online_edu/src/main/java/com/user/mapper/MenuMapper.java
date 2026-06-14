package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.model.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单数据访问层
 */

public interface MenuMapper extends BaseMapper<SysMenu> {
    
    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 统计菜单被角色引用的数量
     *
     * @param menuId 菜单ID
     * @return 引用数量
     */
    int countRoleMenuByMenuId(@Param("menuId") Long menuId);
    
    /**
     * 删除角色菜单关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRoleMenus(@Param("roleId") Long roleId);
    
    /**
     * 批量插入角色菜单关联
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 影响行数
     */
    int insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
}
