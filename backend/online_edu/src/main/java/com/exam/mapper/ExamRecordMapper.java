package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.model.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试记录数据访问层
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
    
    /**
     * 分页查询考试记录
     *
     * @param page 分页参数
     * @param record 查询条件
     * @return 分页数据
     */
    IPage<ExamRecord> selectRecordPage(Page<ExamRecord> page, @Param("record") ExamRecord record);
    
    /**
     * 分页查询教师的考试记录
     *
     * @param page 分页参数
     * @param record 查询条件
     * @param teacherId 教师ID
     * @return 分页数据
     */
    IPage<ExamRecord> selectTeacherRecordPage(Page<ExamRecord> page, @Param("record") ExamRecord record, @Param("teacherId") Long teacherId);
    
    /**
     * 查询考试记录详情
     *
     * @param recordId 记录ID
     * @return 记录信息
     */
    ExamRecord selectRecordById(@Param("recordId") Long recordId);
    
    /**
     * 查询学生的考试记录
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param record 查询条件
     * @return 分页数据
     */
    IPage<ExamRecord> selectStudentRecordPage(Page<ExamRecord> page, @Param("studentId") Long studentId, @Param("record") ExamRecord record);
    
    /**
     * 查询学生在指定考试的记录
     *
     * @param examId 试卷ID
     * @param studentId 学生ID
     * @return 考试记录
     */
    ExamRecord selectStudentRecordByExamId(@Param("examId") Long examId, @Param("studentId") Long studentId);
    
    /**
     * 查询考试的统计信息
     *
     * @param examId 试卷ID
     * @return 统计信息
     */
    List<ExamRecord> selectExamStats(@Param("examId") Long examId);
    
    /**
     * 统计考试的参与人数
     *
     * @param examId 试卷ID
     * @return 参与人数
     */
    int countByExamId(@Param("examId") Long examId);
} 