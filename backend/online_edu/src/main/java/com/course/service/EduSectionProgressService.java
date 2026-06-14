package com.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.core.domain.AjaxResult;
import com.course.model.entity.EduSectionProgress;
import java.util.List;

/**
 * 课程学习进度服务接口
 */
public interface EduSectionProgressService extends IService<EduSectionProgress> {
    
    /**
     * 获取学生在某个小节的学习进度
     * 
     * @param studentId 学生ID
     * @param sectionId 小节ID
     * @return 学习进度记录
     */
    EduSectionProgress getProgressByStudentAndSection(Long studentId, Long sectionId);
    
    /**
     * 获取学生在某个课程的所有学习进度
     * 
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 学习进度列表
     */
    List<EduSectionProgress> getProgressByCourseAndStudent(Long courseId, Long studentId);
    
    /**
     * 计算学生在课程中的总体学习进度
     * 
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 总体学习进度（百分比）
     */
    Integer calculateCourseProgress(Long courseId, Long studentId);
    
    /**
     * 更新学习进度
     * 
     * @param studentId 学生ID
     * @param sectionId 小节ID
     * @param progress 进度百分比
     * @param position 当前位置（秒）
     * @return 更新结果
     */
    AjaxResult updateProgress(Long studentId, Long sectionId, Integer progress, Integer position);
    
    /**
     * 标记小节为已完成
     * 
     * @param studentId 学生ID
     * @param sectionId 小节ID
     * @return 更新结果
     */
    AjaxResult completeSection(Long studentId, Long sectionId);
} 