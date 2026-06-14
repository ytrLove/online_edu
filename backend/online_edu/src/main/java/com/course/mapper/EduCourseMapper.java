package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.model.dto.response.CourseListResponse;
import com.course.model.entity.EduCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import java.util.List;

@Mapper
public interface EduCourseMapper extends BaseMapper<EduCourse> {
    /**
     * 分页查询课程列表，包含学科名称
     * @param page 分页参数
     * @param course 查询条件
     * @return 分页数据
     */
    @ResultMap("CourseListResponseResult")
    IPage<CourseListResponse> selectCourseList(Page<CourseListResponse> page, @Param("course") EduCourse course);
    
    /**
     * 获取课程详情，包含学科名称和教师信息
     * @param courseId 课程ID
     * @return 课程详情
     */
    EduCourse selectCourseById(@Param("courseId") Long courseId);
    
    /**
     * 根据教师ID查询课程列表
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<EduCourse> selectCoursesByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 查询课程的教师ID列表
     * @param courseId 课程ID
     * @return 教师ID列表
     */
    List<Long> selectCourseTeacherIds(@Param("courseId") Long courseId);
    
    /**
     * 逻辑删除课程（将del_flag设置为1）
     * @param courseId 课程ID
     * @return 影响的行数
     */
    int logicDeleteCourse(@Param("courseId") Long courseId);
    
    /**
     * 查询学生最近学习的课程列表（按最近学习时间排序）
     * @param page 分页参数
     * @param studentId 学生ID
     * @return 分页数据
     */
    @ResultMap("CourseListResponseResult")
    IPage<CourseListResponse> selectStudentRecentCourses(Page<CourseListResponse> page, @Param("studentId") Long studentId);

    /**
     * 旧方法，已被废弃，保留是为了兼容性，实际不再调用Mapper中的方法
     * 现在由Service层直接实现，不依赖XML中的SQL
     *
     * @deprecated 使用新的方法和接口
     */
    @Deprecated
    default IPage<EduCourse> listCourses(EduCourse course, Integer pageNum, Integer pageSize) {
        return null;
    }

    int selectResourceCount(Long courseId);
} 