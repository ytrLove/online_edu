package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.model.entity.EduSectionProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EduSectionProgressMapper extends BaseMapper<EduSectionProgress> {
    
    /**
     * 根据学生ID和小节ID查询学习进度
     * 
     * @param studentId 学生ID
     * @param sectionId 小节ID
     * @return 学习进度
     */
    EduSectionProgress selectByStudentAndSection(@Param("studentId") Long studentId, @Param("sectionId") Long sectionId);
    
    /**
     * 根据学生ID和课程ID查询该课程的所有学习进度
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学习进度列表
     */
    List<EduSectionProgress> selectByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    
    /**
     * 计算学生在课程中的总体学习进度
     * 
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 总体学习进度（百分比）
     */
    Integer calculateCourseProgress(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
} 