package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.model.entity.EduStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生数据访问层
 */
@Mapper
public interface EduStudentMapper extends BaseMapper<EduStudent> {
    
    /**
     * 查询学生列表（包含关联用户信息）
     *
     * @return 学生列表
     */
    List<EduStudent> selectStudentsWithUserInfo();
    
    /**
     * 根据ID查询学生（包含关联用户信息）
     *
     * @param studentId 学生ID
     * @return 学生信息
     */
    EduStudent selectStudentWithUserInfoById(@Param("studentId") Long studentId);
} 