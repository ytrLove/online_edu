package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamQuestionBank;
import com.exam.model.param.QuestionParam;
import com.exam.model.vo.ExamQuestionVO;
import com.exam.model.vo.QuestionVO;

import java.util.List;

/**
 * 题库服务接口
 */
public interface ExamQuestionBankService extends IService<ExamQuestionBank> {
    
    /**
     * 分页查询题目列表
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param question 查询条件
     * @return 分页结果
     */
    IPage<ExamQuestionBank> selectQuestionPage(Integer pageNum, Integer pageSize, ExamQuestionBank question);
    
    /**
     * 查询题目详情
     *
     * @param questionId 题目ID
     * @return 题目详情
     */
    ExamQuestionBank selectQuestionById(Long questionId);
    
    /**
     * 查询题目详情（返回VO对象）
     *
     * @param questionId 题目ID
     * @return 题目详情VO
     */
    QuestionVO selectQuestionVOById(Long questionId);
    
    /**
     * 根据题目ID列表批量查询题目
     *
     * @param questionIds 题目ID列表
     * @return 题目列表
     */
    List<ExamQuestionBank> selectQuestionByIds(List<Long> questionIds);
    
    /**
     * 新增题目
     *
     * @param question 题目信息
     * @return 结果
     */
    boolean insertQuestion(QuestionParam questionParam);
    
    /**
     * 修改题目
     *
     * @param question 题目信息
     * @return 结果
     */
    boolean updateQuestion(QuestionParam questionParam);
    
    /**
     * 删除题目
     *
     * @param questionId 题目ID
     * @return 结果
     */
    boolean deleteQuestionById(Long questionId);
    
    /**
     * 批量删除题目
     *
     * @param questionIds 需要删除的题目ID数组
     * @return 结果
     */
    boolean deleteQuestionByIds(Long[] questionIds);
    
    /**
     * 获取作业的题目列表
     * 
     * @param assignmentId 作业ID
     * @return 题目VO列表
     */
    List<ExamQuestionVO> getQuestionsByAssignmentId(Long assignmentId);
} 