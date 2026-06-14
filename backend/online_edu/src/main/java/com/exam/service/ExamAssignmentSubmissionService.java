package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamAssignmentSubmission;
import com.exam.model.param.SubmissionParam;
import com.exam.model.vo.SubmissionVO;

import java.util.List;

/**
 * 作业提交服务接口
 */
public interface ExamAssignmentSubmissionService extends IService<ExamAssignmentSubmission> {
    
    /**
     * 分页查询作业提交记录
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param submission 查询条件
     * @return 分页结果
     */
    IPage<ExamAssignmentSubmission> selectSubmissionPage(Integer pageNum, Integer pageSize, ExamAssignmentSubmission submission);
    
    /**
     * A查询提交详情
     *
     * @param submissionId 提交ID
     * @return 提交详情
     */
    ExamAssignmentSubmission selectSubmissionById(Long submissionId);
    
    /**
     * 查询提交详情（返回VO对象）
     *
     * @param submissionId 提交ID
     * @return 提交详情VO
     */
    SubmissionVO selectSubmissionVOById(Long submissionId);
    
    /**
     * 查询学生的提交记录
     *
     * @param studentId 学生ID
     * @param assignmentId 作业ID
     * @return 提交记录
     */
    ExamAssignmentSubmission selectByStudentAndAssignment(Long studentId, Long assignmentId);
    
    /**
     * 查询作业的提交率统计
     *
     * @param assignmentId 作业ID
     * @return 提交率统计
     */
    List<ExamAssignmentSubmission> selectSubmissionStatsByAssignmentId(Long assignmentId);
    
    /**
     * 学生提交作业
     *
     * @param submissionParam 提交信息
     * @param studentId 学生ID
     * @return 结果
     */
    boolean submitAssignment(SubmissionParam submissionParam, Long studentId);
    
    /**
     * 教师批改作业
     *
     * @param submissionParam 批改信息
     * @return 结果
     */
    boolean gradeSubmission(SubmissionParam submissionParam);
    
    /**
     * 删除提交记录
     *
     * @param submissionId 提交ID
     * @return 结果
     */
    boolean deleteSubmissionById(Long submissionId);
    
    /**
     * 批量删除提交记录
     *
     * @param submissionIds 需要删除的提交ID数组
     * @return 结果
     */
    boolean deleteSubmissionByIds(Long[] submissionIds);
    
    /**
     * 将提交实体转换为VO
     *
     * @param submission 提交实体
     * @return 提交VO
     */
    SubmissionVO convertToVO(ExamAssignmentSubmission submission);
} 