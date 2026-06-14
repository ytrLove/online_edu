package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.model.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface UserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户信息，包含角色信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectUserWithRoles(String username);
    
    /**
     * 删除用户角色关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRoles(Long userId);
    
    /**
     * 批量插入用户角色关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
