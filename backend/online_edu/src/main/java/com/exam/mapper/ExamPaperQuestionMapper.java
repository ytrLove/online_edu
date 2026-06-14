package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.model.entity.ExamPaperQuestion;
import com.exam.model.entity.ExamQuestionBank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 试卷题目关联数据访问层
 */
@Mapper
public interface ExamPaperQuestionMapper extends BaseMapper<ExamPaperQuestion> {
    
    /**
     * 批量插入试卷题目关联
     *
     * @param questions 试卷题目关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<ExamPaperQuestion> questions);
    
    /**
     * 根据试卷ID查询关联的题目
     *
     * @param examId 试卷ID
     * @return 题目列表
     */
    List<ExamQuestionBank> selectQuestionsByExamId(@Param("examId") Long examId);
    
    /**
     * 根据试卷ID删除关联的题目
     *
     * @param examId 试卷ID
     * @return 影响行数
     */
    int deleteByExamId(@Param("examId") Long examId);
} 