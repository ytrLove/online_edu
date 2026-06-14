package com.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.course.mapper.EduStudentMapper;
import com.course.mapper.EduTeacherMapper;
import com.course.model.entity.EduStudent;
import com.course.model.entity.EduTeacher;
import com.user.model.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GetId {

    @Autowired
    private EduStudentMapper studentMapper;
    @Autowired
    private EduTeacherMapper teacherMapper;
    public Long getId() {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取用户id
        SysUser user = (SysUser) authentication.getPrincipal();

        // 判断用户是否为学生
        if (user.getRoles().get(0).getRoleName().equals("学生")) {
            QueryWrapper<EduStudent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", user.getUserId());
            EduStudent student = studentMapper.selectOne(queryWrapper);
            return student.getStudentId();
        }
        // 判断用户是否为教师
        if (user.getRoles().get(0).getRoleName().equals("教师")) {
            QueryWrapper<EduTeacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", user.getUserId());
            EduTeacher teacher = teacherMapper.selectOne(queryWrapper);
            return teacher.getTeacherId();
        }

        return user.getUserId();
    }
}
