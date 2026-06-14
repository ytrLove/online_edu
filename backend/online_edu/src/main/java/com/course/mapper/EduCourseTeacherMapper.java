package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.model.entity.EduCourseTeacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EduCourseTeacherMapper extends BaseMapper<EduCourseTeacher> {
    /**
     * 获取课程的教师列表
     * @param courseId 课程ID
     * @return 教师列表
     */
    List<EduCourseTeacher> selectTeachersByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 获取教师的课程列表
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<EduCourseTeacher> selectCoursesByTeacherId(@Param("teacherId") Long teacherId);
} 