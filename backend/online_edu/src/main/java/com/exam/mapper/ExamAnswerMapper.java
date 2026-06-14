package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.model.entity.ExamAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试答案数据访问层
 */
@Mapper
public interface ExamAnswerMapper extends BaseMapper<ExamAnswer> {
    
    /**
     * 根据记录ID查询答案列表（包含题目信息）
     *
     * @param recordId 记录ID
     * @return 答案列表
     */
    List<ExamAnswer> selectAnswersByRecordId(@Param("recordId") Long recordId);
    
    /**
     * 批量插入考试答案
     *
     * @param answers 答案列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<ExamAnswer> answers);
    
    /**
     * 批量更新考试答案分数
     *
     * @param answers 答案列表
     * @return 影响行数
     */
    int batchUpdateScore(@Param("list") List<ExamAnswer> answers);
} 