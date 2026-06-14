package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.model.entity.ExamAssignmentQuestion;
import com.exam.model.entity.ExamQuestionBank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业题目关联Mapper接口
 */
@Mapper
public interface ExamAssignmentQuestionMapper extends BaseMapper<ExamAssignmentQuestion> {
    
    /**
     * 根据作业ID查询题目列表
     *
     * @param assignmentId 作业ID
     * @return 题目列表
     */
    List<ExamQuestionBank> selectQuestionsByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    /**
     * 批量插入作业题目关联
     *
     * @param assignmentQuestions 作业题目关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ExamAssignmentQuestion> assignmentQuestions);
    
    /**
     * 批量插入作业题目关联（兼容旧方法名）
     *
     * @param assignmentQuestions 作业题目关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<ExamAssignmentQuestion> assignmentQuestions);
    
    /**
     * 根据作业ID删除所有关联的题目
     *
     * @param assignmentId 作业ID
     * @return 影响行数
     */
    int deleteByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    /**
     * 根据作业ID和题目ID删除关联
     *
     * @param assignmentId 作业ID
     * @param questionId 题目ID
     * @return 影响行数
     */
    int deleteByAssignmentIdAndQuestionId(@Param("assignmentId") Long assignmentId, @Param("questionId") Long questionId);
    
    /**
     * 根据作业ID查询所有关联的题目ID
     *
     * @param assignmentId 作业ID
     * @return 题目ID列表
     */
    List<Long> selectQuestionIdsByAssignmentId(@Param("assignmentId") Long assignmentId);
} 