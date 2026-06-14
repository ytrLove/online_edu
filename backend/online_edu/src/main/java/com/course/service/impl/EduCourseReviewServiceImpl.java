package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.mapper.EduCourseMapper;
import com.course.model.entity.EduCourse;
import com.course.service.EduCourseReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 课程审核服务实现类
 */
@Service
public class EduCourseReviewServiceImpl implements EduCourseReviewService {

    @Autowired
    private EduCourseMapper courseMapper;

    @Override
    public IPage<EduCourse> listReviewCourses(Integer pageNum, Integer pageSize, String courseName, String reviewStatus) {
        Page<EduCourse> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        LambdaQueryWrapper<EduCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduCourse::getDelFlag, 0);
        
        // 添加课程名称条件
        if (StringUtils.hasText(courseName)) {
            queryWrapper.like(EduCourse::getCourseName, courseName);
        }
        
        // 添加状态条件
        if (StringUtils.hasText(reviewStatus)) {
            queryWrapper.eq(EduCourse::getStatus, reviewStatus);
        } else {
            // 如果未指定状态，默认查询待审核状态
            queryWrapper.eq(EduCourse::getStatus, "pending");
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(EduCourse::getCreateTime);
        
        // 执行查询
        return courseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveCourse(Long courseId) {
        EduCourse course = new EduCourse();
        course.setCourseId(courseId);
        course.setStatus("published");
        course.setUpdateTime(LocalDateTime.now());
        
        return courseMapper.updateById(course) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectCourse(Long courseId, String reason) {
        EduCourse course = new EduCourse();
        course.setCourseId(courseId);
        course.setStatus("rejected");
        course.setRemark(reason); // 使用remark字段存储拒绝理由
        course.setUpdateTime(LocalDateTime.now());
        
        return courseMapper.updateById(course) > 0;
    }
} 