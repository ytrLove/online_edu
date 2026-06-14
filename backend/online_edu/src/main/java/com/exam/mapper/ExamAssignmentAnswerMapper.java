package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.model.entity.ExamAssignmentAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业答案数据访问层
 */
@Mapper
public interface ExamAssignmentAnswerMapper extends BaseMapper<ExamAssignmentAnswer> {
    
    /**
     * 根据提交ID查询答案列表（包含题目信息）
     *
     * @param submissionId 提交ID
     * @return 答案列表
     */
    List<ExamAssignmentAnswer> selectAnswersBySubmissionId(@Param("submissionId") Long submissionId);
    
    /**
     * 批量插入作业答案
     *
     * @param answers 答案列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<ExamAssignmentAnswer> answers);
    
    /**
     * 批量更新作业答案分数
     *
     * @param answers 答案列表
     * @return 影响行数
     */
    int batchUpdateScore(@Param("list") List<ExamAssignmentAnswer> answers);
} 