package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.mapper.*;
import com.course.model.dto.request.CourseCreateRequest;
import com.course.model.dto.request.CourseQueryRequest;
import com.course.model.dto.request.CourseUpdateRequest;
import com.course.model.dto.response.CourseDetailResponse;
import com.course.model.dto.response.CourseListResponse;
import com.course.model.entity.*;
import com.course.service.EduCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseTeacherMapper courseTeacherMapper;
    
    @Autowired
    private EduCourseStudentMapper courseStudentMapper;
    
    @Autowired
    private EduTeacherMapper teacherMapper;

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Override
    public IPage<CourseListResponse> listCourses(CourseQueryRequest request) {
        Page<CourseListResponse> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        // 构建查询条件
        EduCourse queryParams = new EduCourse();
        
        // 添加查询条件
        if (StringUtils.hasText(request.getCourseName())) {
            queryParams.setCourseName(request.getCourseName());
        }
        if (StringUtils.hasText(request.getCourseCode())) {
            queryParams.setCourseCode(request.getCourseCode());
        }
        if (request.getSubjectId() != null) {
            queryParams.setSubjectId(request.getSubjectId());
        }
        if (StringUtils.hasText(request.getStatus())) {
            queryParams.setStatus(request.getStatus());
        }
        
        // 执行查询，直接返回CourseListResponse
        IPage<CourseListResponse> resultPage = baseMapper.selectCourseList(page, queryParams);
        
        // 为每个课程添加教师ID列表
        if (resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
            for (CourseListResponse course : resultPage.getRecords()) {
                // 查询课程的教师ID列表
                List<Long> teacherIds = baseMapper.selectCourseTeacherIds(course.getCourseId());
                course.setTeacherIds(teacherIds);
            }
        }
        
        // 如果指定了教师ID，需要过滤结果
        if (request.getTeacherId() != null) {
            // 查询教师关联的课程ID
            LambdaQueryWrapper<EduCourseTeacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(EduCourseTeacher::getTeacherId, request.getTeacherId());
            List<EduCourseTeacher> relations = courseTeacherMapper.selectList(teacherWrapper);
            
            if (!relations.isEmpty()) {
                // 获取课程ID列表
                List<Long> courseIds = relations.stream()
                        .map(EduCourseTeacher::getCourseId)
                        .collect(Collectors.toList());
                
                // 过滤结果只保留教师相关课程
                List<CourseListResponse> filteredRecords = resultPage.getRecords().stream()
                        .filter(course -> courseIds.contains(course.getCourseId()))
                        .collect(Collectors.toList());
                
                // 更新结果集
                resultPage.setRecords(filteredRecords);
                resultPage.setTotal(filteredRecords.size());
            } else {
                // 没有关联课程，返回空结果
                resultPage.setRecords(new ArrayList<>());
                resultPage.setTotal(0);
            }
        }
        
        return resultPage;
    }

    @Override
    public CourseDetailResponse getCourseDetailById(Long courseId) {
        // 查询课程基本信息
        EduCourse course = getCourseById(courseId);
        if (course == null) {
            return null;
        }
        
        CourseDetailResponse response = new CourseDetailResponse();
        BeanUtils.copyProperties(course, response);
        
        // 查询课程教师信息
        List<CourseDetailResponse.TeacherInfo> teachers = getCourseTeachers(courseId);
        response.setTeachers(teachers);
        
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailResponse createCourse(CourseCreateRequest request) {
        // 将请求转换为实体
        EduCourse course = new EduCourse();
        BeanUtils.copyProperties(request, course);
        
        // 设置初始状态为草稿（如果未指定）
        if (!StringUtils.hasText(course.getStatus())) {
            course.setStatus("draft");
        }
        
        course.setDelFlag(0);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        
        // 保存课程信息
        baseMapper.insert(course);
        
        // 保存课程教师关联
        saveTeacherRelations(course.getCourseId(), request.getTeacherIds());
        
        // 返回创建后的课程详情
        return getCourseDetailById(course.getCourseId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailResponse updateCourse(CourseUpdateRequest request) {
        // 将请求转换为实体
        EduCourse course = new EduCourse();
        BeanUtils.copyProperties(request, course);
        course.setUpdateTime(LocalDateTime.now());
        
        // 更新课程信息
        baseMapper.updateById(course);
        
        // 更新教师关联
        if (request.getTeacherIds() != null) {
            // 删除旧的教师关联
            LambdaQueryWrapper<EduCourseTeacher> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EduCourseTeacher::getCourseId, course.getCourseId());
            courseTeacherMapper.delete(wrapper);
            
            // 保存新的教师关联
            saveTeacherRelations(course.getCourseId(), request.getTeacherIds());
        }
        
        // 返回更新后的课程详情
        return getCourseDetailById(course.getCourseId());
    }

    @Override
    public IPage<EduCourse> listCourses(EduCourse course, Integer pageNum, Integer pageSize) {
        Page<EduCourse> page = new Page<>(pageNum, pageSize);
        
        // 由于selectCourseList方法签名变更，不能直接调用
        // 使用原生Mybatis-Plus方法查询
        LambdaQueryWrapper<EduCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduCourse::getDelFlag, 0);
        
        if (course != null) {
            if (StringUtils.hasText(course.getCourseName())) {
                queryWrapper.like(EduCourse::getCourseName, course.getCourseName());
            }
            if (StringUtils.hasText(course.getCourseCode())) {
                queryWrapper.eq(EduCourse::getCourseCode, course.getCourseCode());
            }
            if (course.getSubjectId() != null) {
                queryWrapper.eq(EduCourse::getSubjectId, course.getSubjectId());
            }
            if (StringUtils.hasText(course.getStatus())) {
                queryWrapper.eq(EduCourse::getStatus, course.getStatus());
            }
        }
        
        queryWrapper.orderByDesc(EduCourse::getCreateTime);
        
        // 执行查询并返回结果
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public EduCourse getCourseById(Long courseId) {
        EduCourse course = baseMapper.selectCourseById(courseId);
        
        if (course != null) {
            // 查询选修该课程的学生数量
            LambdaQueryWrapper<com.course.model.entity.EduCourseStudent> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(com.course.model.entity.EduCourseStudent::getCourseId, courseId);
            long count = courseStudentMapper.selectCount(queryWrapper);
            course.setStudentCount((int) count);
        }
        
        return course;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduCourse createCourse(EduCourse course, List<Long> teacherIds) {
        // 设置初始状态为草稿
        if (!StringUtils.hasText(course.getStatus())) {
            course.setStatus("draft");
        }
        course.setDelFlag(0);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        
        // 保存课程信息
        baseMapper.insert(course);
        
        // 保存课程教师关联
        saveTeacherRelations(course.getCourseId(), teacherIds);
        
        return course;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduCourse updateCourse(EduCourse course, List<Long> teacherIds) {
        course.setUpdateTime(LocalDateTime.now());
        
        // 更新课程信息
        baseMapper.updateById(course);
        
        // 删除旧的教师关联
        LambdaQueryWrapper<EduCourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduCourseTeacher::getCourseId, course.getCourseId());
        courseTeacherMapper.delete(wrapper);
        
        // 保存新的教师关联
        saveTeacherRelations(course.getCourseId(), teacherIds);
        
        return course;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCourse(Long courseId) {
        // 检查课程是否存在
        EduCourse course = baseMapper.selectById(courseId);
        if (course == null) {
            return false;
        }
        
        // 调用mapper层的逻辑删除方法
        try {
            return baseMapper.logicDeleteCourse(courseId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCourseStatus(Long courseId, String status) {
        EduCourse course = new EduCourse();
        course.setCourseId(courseId);
        course.setStatus(status);
        course.setUpdateTime(LocalDateTime.now());
        
        return baseMapper.updateById(course) > 0;
    }

    @Override
    public List<EduCourse> getCoursesByTeacherId(Long teacherId) {
        // 查询教师关联的课程ID
        LambdaQueryWrapper<EduCourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduCourseTeacher::getTeacherId, teacherId);
        List<EduCourseTeacher> relations = courseTeacherMapper.selectList(wrapper);
        
        if (relations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询课程详情
        List<Long> courseIds = relations.stream()
                .map(EduCourseTeacher::getCourseId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<EduCourse> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.in(EduCourse::getCourseId, courseIds)
                     .eq(EduCourse::getDelFlag, 0)
                     .orderByDesc(EduCourse::getCreateTime);
        
        return baseMapper.selectList(courseWrapper);
    }
    
    @Override
    public IPage<EduCourse> getTeacherCoursesPage(Long teacherId, Integer pageNum, Integer pageSize, EduCourse course) {
        // 查询教师关联的课程ID
        LambdaQueryWrapper<EduCourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduCourseTeacher::getTeacherId, teacherId);
        List<EduCourseTeacher> relations = courseTeacherMapper.selectList(wrapper);
        
        if (relations.isEmpty()) {
            // 返回空的分页结果
            return new Page<>(pageNum, pageSize);
        }
        
        // 获取课程ID列表
        List<Long> courseIds = relations.stream()
                .map(EduCourseTeacher::getCourseId)
                .collect(Collectors.toList());
        
        // 构建查询条件
        LambdaQueryWrapper<EduCourse> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.in(EduCourse::getCourseId, courseIds)
                     .eq(EduCourse::getDelFlag, 0);
                     
        // 添加查询条件，如果有的话
        if (course != null) {
            if (StringUtils.hasText(course.getCourseName())) {
                courseWrapper.like(EduCourse::getCourseName, course.getCourseName());
            }
            if (course.getStatus() != null) {
                courseWrapper.eq(EduCourse::getStatus, course.getStatus());
            }
            if (course.getSubjectId() != null) {
                courseWrapper.eq(EduCourse::getSubjectId, course.getSubjectId());
            }
        }
        
        // 排序
        courseWrapper.orderByDesc(EduCourse::getCreateTime);
        
        // 执行分页查询
        Page<EduCourse> page = new Page<>(pageNum, pageSize);
        Page<EduCourse> result = baseMapper.selectPage(page, courseWrapper);
        
        // 设置学生数量
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (EduCourse eduCourse : result.getRecords()) {
                // 查询该课程的学生数量
                LambdaQueryWrapper<EduCourseStudent> studentWrapper = new LambdaQueryWrapper<>();
                studentWrapper.eq(EduCourseStudent::getCourseId, eduCourse.getCourseId());
                long count = courseStudentMapper.selectCount(studentWrapper);
                eduCourse.setStudentCount((int) count);
            }
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getCourseStudents(Long courseId) {
        // 查询选修该课程的学生
        return courseStudentMapper.selectStudentDetailsByCourseId(courseId);
    }

    @Override
    public boolean enroll(Long courseId, Long studentId) {
        // 检查是否已加入课程
        LambdaQueryWrapper<EduCourseStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduCourseStudent::getCourseId, courseId);
        queryWrapper.eq(EduCourseStudent::getStudentId, studentId);
        if (courseStudentMapper.selectCount(queryWrapper) > 0) {
            return false;
        }
        
        // 保存课程学生关联
        EduCourseStudent courseStudent = new EduCourseStudent();
        courseStudent.setCourseId(courseId);
        courseStudent.setStudentId(studentId);
        
        return courseStudentMapper.insert(courseStudent) > 0;
    }

    @Override
    public int getStudentCourseCount(Long userId) {
        // 查询学生ID
        EduStudent student = eduStudentMapper.selectById(userId);
        if (student == null) {
            return 0;
        }
        
        Long studentId = student.getStudentId(); // 假设有相应字段存储学生ID
        
        // 查询学生已选课程数量
        LambdaQueryWrapper<EduCourseStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduCourseStudent::getStudentId, studentId);
        return Math.toIntExact(courseStudentMapper.selectCount(queryWrapper));
    }

    @Override
    public IPage<CourseListResponse> getStudentRecentCourses(Long userId, Integer pageNum, Integer pageSize) {
        // 查询学生ID
        EduStudent student = eduStudentMapper.selectById(userId);
        if (student == null) {
            return new Page<>();
        }
        
        Long studentId = student.getStudentId(); // 假设有相应字段存储学生ID
        
        // 创建分页对象
        Page<CourseListResponse> page = new Page<>(pageNum, pageSize);
        
        // 查询学生最近学习的课程（按学习时间降序排序）
        return baseMapper.selectStudentRecentCourses(page, studentId);
    }

    /**
     * 保存课程与教师的关联关系
     * @param courseId 课程ID
     * @param teacherIds 教师ID列表
     */
    private void saveTeacherRelations(Long courseId, List<Long> teacherIds) {
        if (teacherIds == null || teacherIds.isEmpty()) {
            return;
        }
        
        List<EduCourseTeacher> relations = new ArrayList<>();
        for (int i = 0; i < teacherIds.size(); i++) {
            EduCourseTeacher relation = new EduCourseTeacher();
            relation.setCourseId(courseId);
            relation.setTeacherId(teacherIds.get(i));
            // 默认第一个教师为主讲
            relation.setRole(i == 0 ? "main" : "assistant");
            relation.setCreateTime(LocalDateTime.now());
            relations.add(relation);
        }
        
        for (EduCourseTeacher relation : relations) {
            courseTeacherMapper.insert(relation);
        }
    }
    
    /**
     * 将EduCourse实体转换为CourseListResponse
     */
    private CourseListResponse convertToCourseListResponse(EduCourse course) {
        CourseListResponse response = new CourseListResponse();
        BeanUtils.copyProperties(course, response);
        
        // 设置主讲教师
        LambdaQueryWrapper<EduCourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduCourseTeacher::getCourseId, course.getCourseId())
               .eq(EduCourseTeacher::getRole, "main");
        EduCourseTeacher mainTeacher = courseTeacherMapper.selectOne(wrapper);
        if (mainTeacher != null) {
            EduTeacher teacher = teacherMapper.selectById(mainTeacher.getTeacherId());
            if (teacher != null) {
                response.setMainTeacherName(teacher.getNickname());
            }
        }
        
        // 设置教师ID列表
        List<Long> teacherIds = baseMapper.selectCourseTeacherIds(course.getCourseId());
        response.setTeacherIds(teacherIds);
        
        return response;
    }
    
    /**
     * 获取课程的教师信息列表
     */
    private List<CourseDetailResponse.TeacherInfo> getCourseTeachers(Long courseId) {
        // 查询教师关联
        LambdaQueryWrapper<EduCourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduCourseTeacher::getCourseId, courseId);
        List<EduCourseTeacher> relations = courseTeacherMapper.selectList(wrapper);
        
        if (relations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为教师信息
        return relations.stream().map(relation -> {
            EduTeacher teacher = teacherMapper.selectById(relation.getTeacherId());
            CourseDetailResponse.TeacherInfo teacherInfo = new CourseDetailResponse.TeacherInfo();
            if (teacher != null) {
                teacherInfo.setTeacherId(teacher.getTeacherId());
                teacherInfo.setTeacherName(teacher.getNickname());
                teacherInfo.setAvatar(teacher.getAvatar());
                teacherInfo.setRole(relation.getRole());
            }
            return teacherInfo;
        }).collect(Collectors.toList());
    }
} 