package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.mapper.EduCourseMapper;
import com.course.mapper.EduCourseStudentMapper;
import com.course.mapper.EduStudentMapper;
import com.course.mapper.EduTeacherMapper;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduStudent;
import com.course.model.entity.EduTeacher;
import com.course.service.EduStudentService;
import com.exam.model.entity.ExamAssignment;
import com.exam.model.entity.ExamAssignmentSubmission;
import com.exam.model.vo.ExamQuestionVO;
import com.exam.model.vo.StudentAssignmentVO;
import com.exam.service.ExamAssignmentService;
import com.exam.service.ExamAssignmentSubmissionService;
import com.exam.service.ExamQuestionBankService;
import com.user.model.entity.SysUser;
import com.user.mapper.UserMapper;
import com.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生管理服务实现
 */
@Service
public class EduStudentServiceImpl extends ServiceImpl<EduStudentMapper, EduStudent> implements EduStudentService {

    @Autowired
    private EduCourseStudentMapper courseStudentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ExamAssignmentService assignmentService;
    @Autowired
    private EduCourseMapper courseMapper;
    @Autowired
    private EduTeacherMapper teacherMapper;
    @Autowired
    private ExamAssignmentSubmissionService submissionService;
    @Autowired
    private ExamQuestionBankService questionBankService;

    /**
     * 分页查询学生列表
     */
    @Override
    public IPage<EduStudent> listStudents(EduStudent student, Integer pageNum, Integer pageSize) {
        // 使用自定义Mapper方法进行联表查询
        Page<EduStudent> page = new Page<>(pageNum, pageSize);
        
        // 先获取满足条件的学生ID列表
        LambdaQueryWrapper<EduStudent> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (student != null) {
            // 学生姓名
            if (StringUtils.hasText(student.getNickname())) {
                wrapper.like(EduStudent::getNickname, student.getNickname());
            }
            // 学号
            if (StringUtils.hasText(student.getStudentNo())) {
                wrapper.eq(EduStudent::getStudentNo, student.getStudentNo());
            }
            // 专业
            if (StringUtils.hasText(student.getMajor())) {
                wrapper.like(EduStudent::getMajor, student.getMajor());
            }
            // 班级
            if (StringUtils.hasText(student.getClassName())) {
                wrapper.like(EduStudent::getClassName, student.getClassName());
            }
            // 状态
            if (student.getStudentStatus() != null) {
                wrapper.eq(EduStudent::getStudentStatus, student.getStudentStatus());
            }
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(EduStudent::getCreateTime);
        
        // 分页查询
        IPage<EduStudent> result = page(page, wrapper);
        
        // 查询每个学生对应的用户信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (EduStudent s : result.getRecords()) {
                EduStudent studentWithUserInfo = baseMapper.selectStudentWithUserInfoById(s.getStudentId());
                if (studentWithUserInfo != null) {
                    // 复制用户相关信息
                    s.setNickname(studentWithUserInfo.getNickname());
                    s.setAvatar(studentWithUserInfo.getAvatar());
                    s.setUsername(studentWithUserInfo.getUsername());
                    s.setEmail(studentWithUserInfo.getEmail());
                    s.setPhone(studentWithUserInfo.getPhone());
                }
            }
        }
        
        return result;
    }

    /**
     * 获取学生详情
     */
    @Override
    public EduStudent getStudentById(Long studentId) {
        return baseMapper.selectStudentWithUserInfoById(studentId);
    }

    /**
     * 创建学生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduStudent createStudent(EduStudent student) {
        student.setCreateTime(LocalDateTime.now());
        
        // 创建系统用户
        com.user.model.entity.SysUser sysUser = new com.user.model.entity.SysUser();
        sysUser.setUsername(student.getStudentNo()); // 使用学号作为用户名
        sysUser.setPassword("123456"); // 设置默认密码
        sysUser.setNickname(student.getNickname());
        sysUser.setEmail(student.getEmail());
        sysUser.setPhone(student.getPhone());
        sysUser.setAvatar(student.getAvatar());
        sysUser.setStatus(1); // 启用状态
        sysUser.setRemark("学生用户");
        
        // 注册用户并分配学生角色
        boolean userCreated = userService.registerUserWithRole(sysUser, List.of(3L)); // 假设学生角色ID为3
        
        if (!userCreated) {
            throw new RuntimeException("创建学生用户失败");
        }
        
        // 设置userId并保存学生信息
        student.setUserId(sysUser.getUserId());
        save(student);
        
        return student;
    }

    /**
     * 更新学生信息
     * @param student 学生信息
     * @return 更新后的学生信息
     * @throws IllegalArgumentException 参数验证失败
     * @throws RuntimeException 更新失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduStudent updateStudent(EduStudent student) {
        // 参数验证
        if (student == null || student.getStudentId() == null) {
            throw new IllegalArgumentException("学生信息不能为空");
        }

        // 验证必填字段
        if (!StringUtils.hasText(student.getStudentNo())) {
            throw new IllegalArgumentException("学号不能为空");
        }

        // 检查学生是否存在
        EduStudent existingStudent = getById(student.getStudentId());
        if (existingStudent == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查学号是否被其他学生使用
        if (!existingStudent.getStudentNo().equals(student.getStudentNo())) {
            LambdaQueryWrapper<EduStudent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EduStudent::getStudentNo, student.getStudentNo())
                  .ne(EduStudent::getStudentId, student.getStudentId());
            if (count(wrapper) > 0) {
                throw new RuntimeException("学号已被其他学生使用");
            }
        }

        try {
            // 设置更新时间
            student.setUpdateTime(LocalDateTime.now());
            
            // 更新学生表
            boolean studentUpdated = updateById(student);
            if (!studentUpdated) {
                throw new RuntimeException("更新学生信息失败");
            }

            // 更新用户表
            Long userId = student.getUserId();
            SysUser user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("关联的用户信息不存在");
            }

            // 检查用户名是否被其他用户使用
            if (StringUtils.hasText(student.getUsername()) && !student.getUsername().equals(user.getUsername())) {
                LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(SysUser::getUsername, student.getUsername())
                         .ne(SysUser::getUserId, userId);
                if (userMapper.selectCount(userWrapper) > 0) {
                    throw new RuntimeException("用户名已被其他用户使用");
                }
            }

            // 更新用户信息
            user.setNickname(student.getNickname());
            user.setAvatar(student.getAvatar());
            user.setUsername(student.getUsername());
            user.setEmail(student.getEmail());
            user.setPhone(student.getPhone());
            user.setUpdateTime(LocalDateTime.now());

            // 验证邮箱格式
            if (user.getEmail() != null && !user.getEmail().isEmpty() && 
                !user.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }

            // 验证手机号格式
            if (user.getPhone() != null && !user.getPhone().isEmpty() && 
                !user.getPhone().matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }

            boolean userUpdated = userMapper.updateById(user) > 0;
            if (!userUpdated) {
                throw new RuntimeException("更新用户信息失败");
            }

            // 返回更新后的完整学生信息
            return getStudentById(student.getStudentId());
        } catch (Exception e) {
            throw new RuntimeException("更新学生信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除学生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudent(Long studentId) {
        // 检查学生是否有选课记录
        List<EduCourse> courses = getStudentCourses(studentId);
        if (courses != null && !courses.isEmpty()) {
            throw new RuntimeException("该学生有关联的课程记录，不能删除");
        }
        return removeById(studentId);
    }

    /**
     * 获取学生的选课列表
     */
    @Override
    public List<EduCourse> getStudentCourses(Long studentId) {
        return courseStudentMapper.selectCourseListByStudentId(studentId);
    }

    /**
     * 获取学生的作业列表
     * 根据学生ID获取所有已选课程，然后查询这些课程下的作业
     * 
     * @param studentId 学生ID
     * @return 作业VO列表
     */
    @Override
    public List<StudentAssignmentVO> getStudentAssignments(Long studentId) {
        // 获取学生选修的所有课程
        List<EduCourse> courses = courseStudentMapper.selectCourseListByStudentId(studentId);
        
        // 创建一个集合用于存储作业VO
        List<StudentAssignmentVO> assignmentVOs = new ArrayList<>();
        
        // 当前时间，用于计算截止状态
        LocalDateTime now = LocalDateTime.now();
        
        if (courses != null && !courses.isEmpty()) {
            // 遍历课程列表，获取每个课程的作业
            for (EduCourse course : courses) {
                // 构建查询条件
                LambdaQueryWrapper<ExamAssignment> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ExamAssignment::getCourseId, course.getCourseId())
                           .eq(ExamAssignment::getStatus, 1)  // 状态正常的作业
                           .eq(ExamAssignment::getDelFlag, 0) // 未删除的作业
                           .orderByDesc(ExamAssignment::getCreateTime); // 按创建时间降序排序
                
                // 查询该课程的作业列表
                List<ExamAssignment> courseAssignments = assignmentService.list(queryWrapper);
                
                // 转换为StudentAssignmentVO并添加额外信息
                if (courseAssignments != null && !courseAssignments.isEmpty()) {
                    for (ExamAssignment assignment : courseAssignments) {
                        StudentAssignmentVO vo = convertToVO(assignment, course, studentId, now);
                        assignmentVOs.add(vo);
                    }
                }
            }
        }
        
        return assignmentVOs;
    }
    
    /**
     * 将ExamAssignment转换为StudentAssignmentVO
     * 
     * @param assignment 作业实体
     * @param course 课程实体
     * @param studentId 学生ID
     * @param now 当前时间
     * @return 作业VO
     */
    private StudentAssignmentVO convertToVO(ExamAssignment assignment, EduCourse course, Long studentId, LocalDateTime now) {
        StudentAssignmentVO vo = new StudentAssignmentVO();
        
        // 复制基本属性
        vo.setAssignmentId(assignment.getAssignmentId());
        vo.setCourseId(assignment.getCourseId());
        vo.setTitle(assignment.getTitle());
        vo.setDescription(assignment.getDescription());
        vo.setStartTime(assignment.getStartTime());
        vo.setDueTime(assignment.getDueTime());
        vo.setTotalPoints(assignment.getTotalPoints());
        vo.setTeacherId(assignment.getTeacherId());
        vo.setStatus(assignment.getStatus());
        vo.setDelFlag(assignment.getDelFlag());
        vo.setCreateTime(assignment.getCreateTime());
        vo.setUpdateTime(assignment.getUpdateTime());
        
        // 设置课程名称
        vo.setCourseName(course.getCourseName());
        
        // 查询并设置教师名称
        if (assignment.getTeacherId() != null) {
            EduTeacher teacher = teacherMapper.selectById(assignment.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getNickname());
            }
        }
        
        // 查询作业问题列表
        List<ExamQuestionVO> questions = questionBankService.getQuestionsByAssignmentId(assignment.getAssignmentId());
        vo.setQuestions(questions);
        
        // 查询学生提交记录
        ExamAssignmentSubmission submission = submissionService.selectByStudentAndAssignment(studentId, assignment.getAssignmentId());
        
        // 设置提交状态
        if (submission == null) {
            vo.setSubmissionStatus("not_submitted");
        } else {
            vo.setSubmissionStatus(submission.getStatus());
            vo.setSubmissionId(submission.getSubmissionId());
            vo.setScore(submission.getTotalScore());
        }
        
        // 计算截止日期状态
        if (now.isBefore(assignment.getStartTime())) {
            vo.setDeadlineStatus("not_started");
        } else if (now.isAfter(assignment.getDueTime())) {
            vo.setDeadlineStatus("expired");
        } else {
            vo.setDeadlineStatus("in_progress");
            
            // 计算剩余时间（小时）
            Duration duration = Duration.between(now, assignment.getDueTime());
            vo.setRemainingHours(duration.toHours());
        }
        
        return vo;
    }
} 