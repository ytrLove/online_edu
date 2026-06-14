package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamAnswer;
import com.exam.model.param.AnswerParam;
import com.exam.model.vo.ExamQuestionVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考试答案服务接口
 */
public interface ExamAnswerService extends IService<ExamAnswer> {
    
    /**
     * 根据考试记录ID查询答案列表
     *
     * @param recordId 考试记录ID
     * @return 答案列表
     */
    List<ExamAnswer> selectAnswersByRecordId(Long recordId);
    
    /**
     * 根据考试记录ID查询答案列表（返回VO对象）
     *
     * @param recordId 考试记录ID
     * @return 答案列表VO
     */
    List<ExamQuestionVO> selectAnswerVOsByRecordId(Long recordId);
    
    /**
     * 批量保存考试答案
     *
     * @param recordId 考试记录ID
     * @param answers 答案列表
     * @return 结果
     */
    boolean batchSaveAnswers(Long recordId, List<AnswerParam> answers);
    
    /**
     * 批量更新考试答案分数
     *
     * @param recordId 考试记录ID
     * @param answers 答案列表
     * @return 结果
     */
    boolean batchUpdateScore(Long recordId, List<AnswerParam> answers);
    
    /**
     * 删除考试答案
     *
     * @param recordId 考试记录ID
     * @return 结果
     */
    boolean deleteAnswersByRecordId(Long recordId);
} 