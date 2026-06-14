package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.course.model.entity.EduCourse;

/**
 * 课程审核服务接口
 */
public interface EduCourseReviewService {

    /**
     * 分页查询待审核课程列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param courseName 课程名称
     * @param reviewStatus 审核状态
     * @return 课程分页信息
     */
    IPage<EduCourse> listReviewCourses(Integer pageNum, Integer pageSize, String courseName, String reviewStatus);

    /**
     * 审核通过课程
     * @param courseId 课程ID
     * @return 是否成功
     */
    boolean approveCourse(Long courseId);

    /**
     * 拒绝课程
     * @param courseId 课程ID
     * @param reason 拒绝理由
     * @return 是否成功
     */
    boolean rejectCourse(Long courseId, String reason);
} 