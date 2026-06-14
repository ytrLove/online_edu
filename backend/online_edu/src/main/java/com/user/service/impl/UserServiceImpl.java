package com.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.entity.SysUser;
import com.user.mapper.RoleMapper;
import com.user.mapper.UserMapper;
import com.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    private final PasswordEncoder passwordEncoder;

    // 使用构造函数注入
    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SysUser getUserByUsername(String username) {
        SysUser user = userMapper.selectUserWithRoles(username);
        if (user != null) {
            // 确保加载角色和菜单权限
            user.setRoles(roleMapper.selectRolesByUserIdWithMenus(user.getUserId()));
        }
        return user;
    }
    
    @Override
    @Transactional
    public boolean registerUser(SysUser user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, user.getUsername());
        if (this.count(queryWrapper) > 0) {
            return false;
        }
        
        // 设置默认值
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1); // 默认启用
        user.setDelFlag(0); // 默认未删除
        user.setCreateTime(LocalDateTime.now());

        // 设置默认角色
      
        return this.save(user);
    }
    
    // 添加带有角色的注册方法
    @Transactional
    public boolean registerUserWithRole(SysUser user, List<Long> roleIds) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, user.getUsername());
        if (this.count(queryWrapper) > 0) {
            return false;
        }
        
        // 设置默认值
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1); // 默认启用
        user.setDelFlag(0); // 默认未删除
        user.setCreateTime(LocalDateTime.now());
        
        // 保存用户
        boolean saveResult = this.save(user);
        if (saveResult && roleIds != null && !roleIds.isEmpty()) {
            // 分配角色
            assignUserRoles(user.getUserId(), roleIds);
        }
        
        return saveResult;
    }
    
    @Override
    @Transactional
    public boolean updateUserInfo(SysUser user) {
        // 不允许修改用户名和密码
        user.setUsername(null);
        user.setPassword(null);
        user.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(user);
    }
    
    @Override
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(user);
    }
    
    @Override
    @Transactional
    public boolean assignUserRoles(Long userId, List<Long> roleIds) {
        // 先删除用户原有角色
        userMapper.deleteUserRoles(userId);
        
        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            userMapper.insertUserRoles(userId, roleIds);
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean deleteUserCompletely(Long userId) {
        // 先删除用户角色关联
        userMapper.deleteUserRoles(userId);
        
        // 再删除用户记录
        return this.removeById(userId);
    }
    
    @Override
    public String toString() {
        return "UserServiceImpl{}";
    }
}