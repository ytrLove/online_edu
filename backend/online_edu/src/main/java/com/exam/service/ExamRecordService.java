package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamRecord;
import com.exam.model.param.RecodeParam;
import com.exam.model.param.SubmissionParam;
import com.exam.model.vo.ExamRecordVO;

import java.util.List;
import java.util.Map;

/**
 * 考试记录服务接口
 */
public interface ExamRecordService extends IService<ExamRecord> {
    
    /**
     * 分页查询考试记录
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param record 查询条件
     * @return 分页结果
     */
    IPage<ExamRecord> selectRecordPage(Integer pageNum, Integer pageSize, ExamRecord record);
    
    /**
     * 查询考试记录详情
     *
     * @param recordId 记录ID
     * @return 考试记录详情
     */
    ExamRecord selectRecordById(Long recordId);
    
    /**
     * 查询考试记录详情（返回VO对象）
     *
     * @param recordId 记录ID
     * @return 考试记录详情VO
     */
    ExamRecordVO selectRecordVOById(Long recordId);
    
    /**
     * 查询学生的考试记录
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param studentId 学生ID
     * @param record 查询条件
     * @return 分页结果
     */
    IPage<ExamRecord> selectStudentRecordPage(Integer pageNum, Integer pageSize, Long studentId, ExamRecord record);
    
    /**
     * 查询学生在指定考试的记录
     *
     * @param examId 试卷ID
     * @param studentId 学生ID
     * @return 考试记录
     */
    ExamRecord selectStudentRecordByExamId(Long examId, Long studentId);
    
    /**
     * 查询考试的统计信息
     *
     * @param examId 试卷ID
     * @return 统计信息
     */
    List<ExamRecord> selectExamStats(Long examId);
    
    /**
     * 学生开始考试
     *
     * @param examId 试卷ID
     * @param studentId 学生ID
     * @return 考试记录ID
     */
    Long startExam(Long examId, Long studentId);
    
    /**
     * 学生提交考试
     *
     * @param submissionParam 提交信息
     * @param studentId 学生ID
     * @return 结果
     */
    boolean submitExam(SubmissionParam submissionParam, Long studentId);
    
    /**
     * 教师批改考试
     *
     * @param recodeParam 批改信息
     * @return 结果
     */

    boolean gradeExam(RecodeParam recodeParam);
    
    /**
     * 删除考试记录
     *
     * @param recordId 记录ID
     * @return 结果
     */
    boolean deleteRecordById(Long recordId);
    
    /**
     * 批量删除考试记录
     *
     * @param recordIds 需要删除的记录ID数组
     * @return 结果
     */
    boolean deleteRecordByIds(Long[] recordIds);
    
    /**
     * 将考试记录实体转换为VO
     *
     * @param record 考试记录实体
     * @return 考试记录VO
     */
    ExamRecordVO convertToVO(ExamRecord record);
} 