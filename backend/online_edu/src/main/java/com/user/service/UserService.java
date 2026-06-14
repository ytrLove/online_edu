package com.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.entity.SysUser;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<SysUser> {
    
    /**
     * 根据用户名查询用户信息，包含角色和权限信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);
    
    /**
     * 注册用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean registerUser(SysUser user);
    
    /**
     * 注册用户并分配角色
     *
     * @param user 用户信息
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean registerUserWithRole(SysUser user, List<Long> roleIds);
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUserInfo(SysUser user);
    
    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String newPassword);
    
    /**
     * 分配用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignUserRoles(Long userId, List<Long> roleIds);
    
    /**
     * 物理删除用户及其关联的角色记录
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUserCompletely(Long userId);
}