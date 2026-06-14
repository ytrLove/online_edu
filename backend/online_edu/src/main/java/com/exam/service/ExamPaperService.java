package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamPaper;
import com.exam.model.entity.ExamPaperQuestion;
import com.exam.model.param.ExamPaperParam;
import com.exam.model.vo.ExamPaperVO;
import com.exam.model.vo.ExamQuestionVO;
import com.exam.model.param.ExamQuestionParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 考试试卷服务接口
 */
public interface ExamPaperService extends IService<ExamPaper> {
    
    /**
     * 分页查询试卷列表
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param exam 查询条件
     * @return 分页结果
     */
    IPage<ExamPaper> selectExamPage(Integer pageNum, Integer pageSize, ExamPaper exam);
    
    /**
     * 查询试卷详情
     *
     * @param examId 试卷ID
     * @return 试卷详情
     */
    ExamPaper selectExamById(Long examId);
    
    /**
     * 查询试卷详情（返回VO对象）
     *
     * @param examId 试卷ID
     * @return 试卷详情VO
     */
    ExamPaperVO selectExamVOById(Long examId);
    
    /**
     * 查询学生的试卷列表
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param studentId 学生ID
     * @param exam 查询条件
     * @return 分页结果
     */
    IPage<ExamPaper> selectStudentExamPage(Integer pageNum, Integer pageSize, Long studentId, ExamPaper exam);
    
    /**
     * 查询学生的试卷详情
     *
     * @param examId 试卷ID
     * @param studentId 学生ID
     * @return 试卷详情VO
     */
    ExamPaperVO selectStudentExamVOById(Long examId, Long studentId);
    
    /**
     * 新增试卷
     *
     * @param examParam 试卷信息
     * @return 结果
     */
    boolean insertExam(ExamPaperParam examParam);
    
    /**
     * 修改试卷
     *
     * @param examParam 试卷信息
     * @return 结果
     */
    boolean updateExam(ExamPaperParam examParam);
    
    /**
     * 删除试卷
     *
     * @param examId 试卷ID
     * @return 结果
     */
    boolean deleteExamById(Long examId);
    
    /**
     * 批量删除试卷
     *
     * @param examIds 需要删除的试卷ID数组
     * @return 结果
     */
    boolean deleteExamByIds(Long[] examIds);
    
    /**
     * 根据试卷ID查询试卷试题列表
     *
     * @param examId 试卷ID
     * @return 试题列表
     */
    List<ExamQuestionVO> selectExamQuestionList(Long examId);
    
    /**
     * 批量更新试卷题目
     *
     * @param examId 试卷ID
     * @param questionId 更新人（由于实体类限制，该参数仅用于记录，不会实际更新到数据库）
     * @param points 分数
     * @return 结果
     */
    boolean updateExamQuestions(Long examId,Long questionId, BigDecimal points);

    boolean insertExamQuestions(List<ExamPaperQuestion> examParam);

    boolean deleteExamQuestion(Long examId, Long questionId);

    boolean unpublishExam(Long examId);

    boolean publishExam(Long examId);
}