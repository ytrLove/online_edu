package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduCourseStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface EduCourseStudentMapper extends BaseMapper<EduCourseStudent> {
    /**
     * 分页查询课程的学生列表
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 学生列表
     */
    IPage<EduCourseStudent> selectStudentsByCourseId(Page<EduCourseStudent> page, @Param("courseId") Long courseId);
    
    /**
     * 分页查询学生的课程列表
     * @param page 分页参数
     * @param studentId 学生ID
     * @return 课程列表
     */
    IPage<EduCourseStudent> selectCoursesByStudentId(Page<EduCourseStudent> page, @Param("studentId") Long studentId);
    
    /**
     * 根据课程ID和学生ID查询关联记录
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 关联记录
     */
    EduCourseStudent selectByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
    
    /**
     * 查询学生选修的所有课程
     * @param studentId 学生ID
     * @return 课程列表
     */
    List<EduCourse> selectCourseListByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 查询选修课程的学生详细信息
     * @param courseId 课程ID
     * @return 学生详情列表
     */
    List<Map<String, Object>> selectStudentDetailsByCourseId(@Param("courseId") Long courseId);
}