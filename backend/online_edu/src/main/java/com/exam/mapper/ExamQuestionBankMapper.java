package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.model.entity.ExamQuestionBank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题库数据访问层
 */
@Mapper
public interface ExamQuestionBankMapper extends BaseMapper<ExamQuestionBank> {
    
    /**
     * 分页查询题目列表（包含题型、学科、教师信息）
     *
     * @param page 分页参数
     * @param question 查询条件
     * @return 题目列表
     */
    IPage<ExamQuestionBank> selectQuestionPage(Page<ExamQuestionBank> page, @Param("question") ExamQuestionBank question);
    
    /**
     * 根据ID查询题目详情（包含题型、学科、教师信息）
     *
     * @param questionId 题目ID
     * @return 题目详情
     */
    ExamQuestionBank selectQuestionById(@Param("questionId") Long questionId);
    
    /**
     * 根据题目ID列表批量查询题目
     *
     * @param questionIds 题目ID列表
     * @return 题目列表
     */
    List<ExamQuestionBank> selectQuestionByIds(@Param("questionIds") List<Long> questionIds);
} 