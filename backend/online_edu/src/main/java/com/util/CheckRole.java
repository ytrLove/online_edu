package com.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.course.mapper.EduStudentMapper;
import com.course.mapper.EduTeacherMapper;
import com.course.model.entity.EduStudent;
import com.course.model.entity.EduTeacher;
import com.user.mapper.UserMapper;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CheckRole {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EduStudentMapper studentMapper;
    @Autowired
    private EduTeacherMapper teacherMapper;


    public boolean isStudent() {
        // 获取当前用户
        Long currentUserId = getCurrentUser();

        // 判断当前用户是否是学生
        if (currentUserId != null) {
            QueryWrapper<EduStudent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", currentUserId);
            EduStudent user = studentMapper.selectOne(queryWrapper);
            return user != null;
        }
        return false;
    }

    public boolean isTeacher() {
        // 获取当前用户
        Long currentUserId = getCurrentUser();

        // 获取当前用户角色
        QueryWrapper<EduTeacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId);
        EduTeacher user = teacherMapper.selectOne(queryWrapper);
        return user != null;
    }

    public boolean isAdmin() {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SysUser) {
            SysUser user = (SysUser) authentication.getPrincipal();
            return user.getRoles().stream()
                    .anyMatch(role -> "admin".equals(role.getRoleKey()));
        }
        return false;
    }

    private Long getCurrentUser() {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();
        if (user != null) {
            return user.getUserId();
        }
        return null;
    }
}
