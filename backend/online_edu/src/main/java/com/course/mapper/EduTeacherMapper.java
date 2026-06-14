package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.model.entity.EduTeacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教师数据访问层
 */
@Mapper
public interface EduTeacherMapper extends BaseMapper<EduTeacher> {
    
    /**
     * 查询教师列表（包含关联用户信息）
     *
     * @return 教师列表
     */
    List<EduTeacher> selectTeachersWithUserInfo();
    
    /**
     * 根据ID查询教师（包含关联用户信息）
     *
     * @param teacherId 教师ID
     * @return 教师信息
     */
    EduTeacher selectTeacherWithUserInfoById(@Param("teacherId") Long teacherId);
} 