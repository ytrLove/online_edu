package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamAssignmentAnswer;
import com.exam.model.param.AnswerParam;
import com.exam.model.vo.AssignmentQuestionVO;

import java.util.List;

/**
 * 作业答案服务接口
 */
public interface ExamAssignmentAnswerService extends IService<ExamAssignmentAnswer> {
    
    /**
     * 根据提交ID查询答案列表
     *
     * @param submissionId 提交ID
     * @return 答案列表
     */
    List<ExamAssignmentAnswer> selectAnswersBySubmissionId(Long submissionId);
    
    /**
     * 根据提交ID查询答案列表（返回VO对象）
     *
     * @param submissionId 提交ID
     * @return 答案列表VO
     */
    List<AssignmentQuestionVO> selectAnswerVOsBySubmissionId(Long submissionId);
    
    /**
     * 批量保存作业答案
     *
     * @param submissionId 提交ID
     * @param answers 答案列表
     * @return 结果
     */
    boolean batchSaveAnswers(Long submissionId, List<AnswerParam> answers);
    
    /**
     * 批量更新作业答案分数
     *
     * @param submissionId 提交ID
     * @param answers 答案列表
     * @return 结果
     */
    boolean batchUpdateScore(Long submissionId, List<AnswerParam> answers);
    
    /**
     * 删除作业答案
     *
     * @param submissionId 提交ID
     * @return 结果
     */
    boolean deleteAnswersBySubmissionId(Long submissionId);
}