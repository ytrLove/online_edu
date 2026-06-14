package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.model.entity.ExamAssignmentSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业提交数据访问层
 */
@Mapper
public interface ExamAssignmentSubmissionMapper extends BaseMapper<ExamAssignmentSubmission> {
    
    /**
     * 分页查询作业提交记录（包含学生、作业信息）
     *
     * @param page 分页参数
     * @param submission 查询条件
     * @return 作业提交记录
     */
    IPage<ExamAssignmentSubmission> selectSubmissionPage(Page<ExamAssignmentSubmission> page, @Param("submission") ExamAssignmentSubmission submission);
    
    /**
     * 根据ID查询提交详情（包含学生、作业信息）
     *
     * @param submissionId 提交ID
     * @return 提交详情
     */
    ExamAssignmentSubmission selectSubmissionById(@Param("submissionId") Long submissionId);
    
    /**
     * 查询学生的提交记录
     *
     * @param studentId 学生ID
     * @param assignmentId 作业ID
     * @return 提交记录
     */
    ExamAssignmentSubmission selectByStudentAndAssignment(@Param("studentId") Long studentId, @Param("assignmentId") Long assignmentId);
    
    /**
     * 查询作业的提交率统计
     *
     * @param assignmentId 作业ID
     * @return 提交率统计
     */
    List<ExamAssignmentSubmission> selectSubmissionStatsByAssignmentId(@Param("assignmentId") Long assignmentId);

    /**
     * 分页查询教师课程下的作业提交记录
     *
     * @param page 分页参数
     * @param submission 查询条件
     * @param teacherId 教师ID
     * @return 分页结果
     */
    IPage<ExamAssignmentSubmission> selectTeacherSubmissionPage(Page<ExamAssignmentSubmission> page, @Param("submission") ExamAssignmentSubmission submission, @Param("teacherId") Long teacherId);
} 