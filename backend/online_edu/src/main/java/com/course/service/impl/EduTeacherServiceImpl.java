package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.mapper.EduCourseMapper;
import com.course.mapper.EduTeacherMapper;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduTeacher;
import com.course.service.EduTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师管理服务实现
 */
@Service
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher> implements EduTeacherService {

    @Autowired
    private EduCourseMapper courseMapper;

    @Autowired
    private com.user.service.UserService userService;

    /**
     * 分页查询教师列表
     */
    @Override
    public IPage<EduTeacher> listTeachers(EduTeacher teacher, Integer pageNum, Integer pageSize) {
        // 使用自定义Mapper方法进行联表查询
        Page<EduTeacher> page = new Page<>(pageNum, pageSize);
        
        // 先获取满足条件的教师ID列表
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (teacher != null) {
            // 教师姓名
            if (StringUtils.hasText(teacher.getNickname())) {
                wrapper.like(EduTeacher::getNickname, teacher.getNickname());
            }
            // 教师工号
            if (StringUtils.hasText(teacher.getTeacherCode())) {
                wrapper.eq(EduTeacher::getTeacherCode, teacher.getTeacherCode());
            }
            // 所属院系
            if (StringUtils.hasText(teacher.getDepartment())) {
                wrapper.like(EduTeacher::getDepartment, teacher.getDepartment());
            }
            // 职称
            if (StringUtils.hasText(teacher.getTitle())) {
                wrapper.eq(EduTeacher::getTitle, teacher.getTitle());
            }
            // 状态
            if (StringUtils.hasText(teacher.getTeacherStatus())) {
                wrapper.eq(EduTeacher::getTeacherStatus, teacher.getTeacherStatus());
            }
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(EduTeacher::getCreateTime);
        
        // 分页查询
        IPage<EduTeacher> result = page(page, wrapper);
        
        // 查询每个教师对应的用户信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (EduTeacher t : result.getRecords()) {
                EduTeacher teacherWithUserInfo = baseMapper.selectTeacherWithUserInfoById(t.getTeacherId());
                if (teacherWithUserInfo != null) {
                    // 复制用户相关信息
                    t.setNickname(teacherWithUserInfo.getNickname());
                    t.setAvatar(teacherWithUserInfo.getAvatar());
                    t.setUsername(teacherWithUserInfo.getUsername());
                    t.setEmail(teacherWithUserInfo.getEmail());
                    t.setPhone(teacherWithUserInfo.getPhone());
                }
            }
        }
        
        return result;
    }

    /**
     * 获取教师详情
     */
    @Override
    public EduTeacher getTeacherById(Long teacherId) {
        return baseMapper.selectTeacherWithUserInfoById(teacherId);
    }

    /**
     * 创建教师
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduTeacher createTeacher(EduTeacher teacher) {
        teacher.setCreateTime(LocalDateTime.now());
        
        // 创建系统用户
        com.user.model.entity.SysUser sysUser = new com.user.model.entity.SysUser();
        sysUser.setUsername(teacher.getTeacherCode()); // 使用教师工号作为用户名
        sysUser.setPassword("123456"); // 设置默认密码
        sysUser.setNickname(teacher.getNickname());
        sysUser.setEmail(teacher.getEmail());
        sysUser.setPhone(teacher.getPhone());
        sysUser.setAvatar(teacher.getAvatar());
        sysUser.setStatus(1); // 启用状态
        sysUser.setRemark("教师用户");
        
        // 注册用户并分配教师角色
        boolean userCreated = userService.registerUserWithRole(sysUser, List.of(2L)); // 假设教师角色ID为2
        
        if (!userCreated) {
            throw new RuntimeException("创建教师用户失败");
        }
        
        // 设置userId并保存教师信息
        teacher.setUserId(sysUser.getUserId());
        save(teacher);
        
        return teacher;
    }

    /**
     * 更新教师信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduTeacher updateTeacher(EduTeacher teacher) {
        teacher.setUpdateTime(LocalDateTime.now());
        
        // 更新教师表信息
        updateById(teacher);
        
        // 同步更新sys_user表信息
        if (teacher.getUserId() != null) {
            com.user.model.entity.SysUser sysUser = userService.getById(teacher.getUserId());
            if (sysUser != null) {
                // 更新sys_user表中的用户信息
                sysUser.setNickname(teacher.getNickname());
                sysUser.setEmail(teacher.getEmail());
                sysUser.setPhone(teacher.getPhone());
                
                // 如果有头像更新，则更新头像
                if (StringUtils.hasText(teacher.getAvatar())) {
                    sysUser.setAvatar(teacher.getAvatar());
                }
                
                // 调用用户服务更新用户信息
                userService.updateUserInfo(sysUser);
            }
        }
        
        return teacher;
    }

    /**
     * 删除教师
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeacher(Long teacherId) {
        // 检查教师是否有关联的课程
        List<EduCourse> courses = getTeacherCourses(teacherId);
        if (courses != null && !courses.isEmpty()) {
            throw new RuntimeException("该教师有关联的课程，不能删除");
        }
        return removeById(teacherId);
    }

    @Override
    public List<EduCourse> getTeacherCourses(Integer pageNum, Integer pageSize, EduCourse course) {
        IPage<EduCourse> page = courseMapper.listCourses(course, pageNum, pageSize);
        if (page != null) {
            return page.getRecords();
        }
        return List.of();
    }

    /**
     * 获取教师的课程列表
     */
    @Override
    public List<EduCourse> getTeacherCourses(Long teacherId) {
        return courseMapper.selectCoursesByTeacherId(teacherId);
    }

    /**
     * 根据用户ID获取教师信息
     */
    @Override
    public EduTeacher getTeacherByUserId(Long userId) {
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacher::getUserId, userId);
        return getOne(wrapper);
    }
} 