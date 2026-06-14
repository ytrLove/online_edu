package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.model.entity.ExamPaper;
import com.exam.model.entity.ExamQuestionBank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试试卷数据访问层
 */
@Mapper
public interface ExamPaperMapper extends BaseMapper<ExamPaper> {
    
    /**
     * 分页查询试卷
     *
     * @param page 分页参数
     * @param exam 查询条件
     * @return 分页数据
     */
    IPage<ExamPaper> selectExamPage(Page<ExamPaper> page, @Param("exam") ExamPaper exam);
    
    /**
     * 查询试卷详情
     *
     * @param examId 试卷ID
     * @return 试卷信息
     */
    ExamPaper selectExamById(@Param("examId") Long examId);
    
    /**
     * 查询学生试卷列表
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param exam 查询条件
     * @return 分页数据
     */
    IPage<ExamPaper> selectStudentExamPage(Page<ExamPaper> page, @Param("studentId") Long studentId, @Param("exam") ExamPaper exam);
    
    /**
     * 根据试卷ID查询题目列表
     *
     * @param examId 试卷ID
     * @return 题目列表
     */
    List<ExamQuestionBank> selectQuestionsByExamId(@Param("examId") Long examId);
} 