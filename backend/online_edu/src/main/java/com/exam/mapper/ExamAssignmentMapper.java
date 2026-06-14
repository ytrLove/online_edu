package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.model.entity.ExamAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 作业数据访问层
 */
@Mapper
public interface ExamAssignmentMapper extends BaseMapper<ExamAssignment> {
    
    /**
     * 分页查询作业列表（包含课程、教师信息）
     *
     * @param page 分页参数
     * @param assignment 查询条件
     * @return 作业列表
     */
    IPage<ExamAssignment> selectAssignmentPage(Page<ExamAssignment> page, @Param("assignment") ExamAssignment assignment);
    
    /**
     * 根据ID查询作业详情（包含课程、教师信息）
     *
     * @param assignmentId 作业ID
     * @return 作业详情
     */
    ExamAssignment selectAssignmentById(@Param("assignmentId") Long assignmentId);
    
    /**
     * 查询学生的作业列表（包含课程、教师信息）
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param assignment 查询条件
     * @return 作业列表
     */
    IPage<ExamAssignment> selectStudentAssignmentPage(Page<ExamAssignment> page, @Param("studentId") Long studentId, @Param("assignment") ExamAssignment assignment);
} 