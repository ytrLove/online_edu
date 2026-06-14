package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.mapper.ExamPaperMapper;
import com.exam.mapper.ExamRecordMapper;
import com.exam.model.constant.ExamConstants;
import com.exam.model.constant.QuestionConstants;
import com.exam.model.entity.ExamAnswer;
import com.exam.model.entity.ExamPaper;
import com.exam.model.entity.ExamRecord;
import com.exam.model.param.RecodeParam;
import com.exam.model.param.SubmissionParam;
import com.exam.model.vo.ExamQuestionVO;
import com.exam.model.vo.ExamRecordVO;
import com.exam.service.ExamAnswerService;
import com.exam.service.ExamRecordService;
import com.course.service.EduTeacherService;
import com.course.model.entity.EduTeacher;
import com.util.CheckRole;
import com.util.GetId;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.user.model.entity.SysUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试记录服务实现类
 */

@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private ExamPaperMapper paperMapper;
    
    @Autowired
    private ExamAnswerService answerService;

    @Autowired
    private CheckRole checkRole;

    @Autowired
    private GetId getId;

    @Autowired
    private EduTeacherService teacherService;
    
    @Override
    public IPage<ExamRecord> selectRecordPage(Integer pageNum, Integer pageSize, ExamRecord record) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamRecord> page = new Page<>(pageNum, pageSize);

        // 获取当前用户ID
        Long userId = getCurrentUserId();
        
        // 根据用户角色返回不同的记录列表
        if (checkRole.isTeacher()) {
            // 获取教师信息
            EduTeacher teacher = teacherService.getTeacherByUserId(userId);
            if (teacher != null) {
                // 返回该教师负责的课程的考试记录
                return baseMapper.selectTeacherRecordPage(page, record, teacher.getTeacherId());
            }
        } else if (checkRole.isAdmin()) {
            // 管理员可以查看所有记录
            return baseMapper.selectRecordPage(page, record);
        }
        
        // 其他角色返回空列表
        return new Page<>();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = (SysUser) authentication.getPrincipal();
        if (user != null) {
            return user.getUserId();
        }
        return null;
    }

    @Override
    public ExamRecord selectRecordById(Long recordId) {
        return baseMapper.selectRecordById(recordId);
    }

    @Override
    public ExamRecordVO selectRecordVOById(Long recordId) {
        ExamRecord record = this.selectRecordById(recordId);
        if (record == null) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    public IPage<ExamRecord> selectStudentRecordPage(Integer pageNum, Integer pageSize, Long studentId, ExamRecord record) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamRecord> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectStudentRecordPage(page, studentId, record);
    }

    @Override
    public ExamRecord selectStudentRecordByExamId(Long examId, Long studentId) {
        return baseMapper.selectStudentRecordByExamId(examId, studentId);
    }

    @Override
    public List<ExamRecord> selectExamStats(Long examId) {
        return baseMapper.selectExamStats(examId);
    }

    @Override
    @Transactional
    public Long startExam(Long examId, Long studentId) {
        // 查询试卷信息
        ExamPaper exam = paperMapper.selectById(examId);
        if (exam == null) {
            return null;
        }
        
        // 检查考试状态（只检查结束时间，允许提前进入考试）
        LocalDateTime now = LocalDateTime.now();
        
        // 移除考试开始时间检查，允许学生提前进入考试
        // if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
        //     // 考试未开始
        //     return null;
        // }
        
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            // 考试已结束
            return null;
        }
        
        // 检查是否已存在考试记录
        ExamRecord existingRecord = baseMapper.selectStudentRecordByExamId(examId, studentId);
        if (existingRecord != null) {
            // 判断是否已提交
            if (existingRecord.getStatus() != null && 
                (existingRecord.getStatus().equals(ExamConstants.EXAM_STATUS_SUBMITTED) || 
                 existingRecord.getStatus().equals(ExamConstants.EXAM_STATUS_GRADED))) {
                return null; // 已提交，不能再次开始
            }
            return existingRecord.getRecordId();
        }
        
        // 创建新的考试记录
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setStartTime(now);
        record.setStatus(ExamConstants.EXAM_STATUS_IN_PROGRESS);
        
        save(record);
        return record.getRecordId();
    }

    @Override
    @Transactional
    public boolean submitExam(SubmissionParam submissionParam, Long studentId) {
        Long recordId = submissionParam.getSubmissionId();
        
        // 查询考试记录
        ExamRecord record = getById(recordId);
        if (record == null || !record.getStudentId().equals(studentId)) {
            return false;
        }

        //
        record.setCreateTime(LocalDateTime.now());
        // 设置提交时间
        LocalDateTime now = LocalDateTime.now();
        record.setSubmitTime(now);
        
        // 更新提交状态
        record.setStatus(ExamConstants.EXAM_STATUS_SUBMITTED);
        
        // 批量保存答案
        boolean saveAnswerResult = false;
        if (!CollectionUtils.isEmpty(submissionParam.getAnswers())) {
            saveAnswerResult = answerService.batchSaveAnswers(recordId, submissionParam.getAnswers());
        }
        
        // 如果保存答案成功，计算自动评分题目的得分总和
        if (saveAnswerResult) {
            List<ExamAnswer> answers = answerService.selectAnswersByRecordId(recordId);
            BigDecimal totalScore = BigDecimal.ZERO;
            boolean hasUngraded = false;
            
            for (ExamAnswer answer : answers) {
                if (answer.getScore() != null) {
                    totalScore = totalScore.add(answer.getScore());
                } else {
                    hasUngraded = true; // 存在未评分的题目
                }
            }
            
            // 如果所有题目都已自动评分，更新状态为已批改
            if (!hasUngraded) {
                record.setStatus(ExamConstants.EXAM_STATUS_GRADED);
            }
            
            // 设置总分
            record.setTotalScore(totalScore);
        }
        
        // 保存考试记录
        return updateById(record);
    }

    @Override
    @Transactional
    public boolean gradeExam(RecodeParam recodeParam) {
        Long recordId = recodeParam.getRecordId();
        
        // 查询考试记录
        ExamRecord record = getById(recordId);
        if (record == null) {
            return false;
        }
        
        // 计算总分
        if (!CollectionUtils.isEmpty(recodeParam.getAnswers())) {
            BigDecimal totalScore = BigDecimal.ZERO;
            for (com.exam.model.param.AnswerParam answerParam : recodeParam.getAnswers()) {
                if (answerParam.getScore() != null) {
                    totalScore = totalScore.add(answerParam.getScore());
                }
            }
            record.setTotalScore(totalScore);
        }
        
        // 更新批改状态
        record.setStatus(ExamConstants.EXAM_STATUS_GRADED);

        record.setUpdateTime(LocalDateTime.now());
        // 更新考试记录
        boolean result = updateById(record);
        if (!result) {
            return false;
        }
        
        // 批量更新答案分数
        if (!CollectionUtils.isEmpty(recodeParam.getAnswers())) {
            return answerService.batchUpdateScore(recordId, recodeParam.getAnswers());
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean deleteRecordById(Long recordId) {
        // 删除答案
        answerService.deleteAnswersByRecordId(recordId);
        // 删除考试记录
        return removeById(recordId);
    }

    @Override
    @Transactional
    public boolean deleteRecordByIds(Long[] recordIds) {
        for (Long recordId : recordIds) {
            deleteRecordById(recordId);
        }
        return true;
    }

    @Override
    public ExamRecordVO convertToVO(ExamRecord record) {
        if (record == null) {
            return null;
        }
        
        ExamRecordVO vo = new ExamRecordVO();
        BeanUtils.copyProperties(record, vo);
        
        // 查询考试信息
        ExamPaper exam = paperMapper.selectById(record.getExamId());
        if (exam != null) {
            vo.setExamTitle(exam.getTitle());
            
            // 计算剩余时间
            if (exam.getDuration() != null && record.getStartTime() != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expectedEndTime = record.getStartTime().plusMinutes(exam.getDuration());
                
                if (now.isBefore(expectedEndTime)) {
                    long remainingSeconds = ChronoUnit.SECONDS.between(now, expectedEndTime);
                    vo.setRemainingTime(remainingSeconds);
                } else {
                    vo.setRemainingTime(0L);
                }
            }
        }
        
        // status 字段已经是字符串类型，可以直接使用
        
        // 查询答案
        List<ExamQuestionVO> answerVOs = answerService.selectAnswerVOsByRecordId(record.getRecordId());
        vo.setAnswers(answerVOs);
        
        return vo;
    }
} 