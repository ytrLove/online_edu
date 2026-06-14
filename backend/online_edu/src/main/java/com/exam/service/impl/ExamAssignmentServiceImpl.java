package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.mapper.EduCourseStudentMapper;
import com.course.model.entity.EduCourseStudent;
import com.exam.mapper.ExamAssignmentMapper;
import com.exam.mapper.ExamAssignmentQuestionMapper;
import com.exam.mapper.ExamAssignmentSubmissionMapper;
import com.exam.mapper.ExamQuestionBankMapper;
import com.exam.model.constant.QuestionConstants;
import com.exam.model.entity.ExamAssignment;
import com.exam.model.entity.ExamAssignmentQuestion;
import com.exam.model.entity.ExamAssignmentSubmission;
import com.exam.model.entity.ExamQuestionBank;
import com.exam.model.param.AssignmentParam;
import com.exam.model.param.AssignmentQuestionParam;
import com.exam.model.vo.AssignmentQuestionVO;
import com.exam.model.vo.AssignmentVO;
import com.exam.model.vo.QuestionVO;
import com.exam.model.vo.SubmissionVO;
import com.exam.service.ExamAssignmentAnswerService;
import com.exam.service.ExamAssignmentService;
import com.exam.service.ExamAssignmentSubmissionService;
import com.exam.service.ExamQuestionBankService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业服务实现类
 */
@Service
public class ExamAssignmentServiceImpl extends ServiceImpl<ExamAssignmentMapper, ExamAssignment> implements ExamAssignmentService {

    @Autowired
    private ExamAssignmentQuestionMapper assignmentQuestionMapper;
    
    @Autowired
    private ExamQuestionBankMapper questionBankMapper;
    
    @Autowired
    private ExamAssignmentSubmissionMapper submissionMapper;
    
    @Autowired
    private ExamAssignmentSubmissionService submissionService;
    
    @Autowired
    private ExamAssignmentAnswerService answerService;

    @Autowired
    private EduCourseStudentMapper eduCourseStudentMapper;
    
    @Override
    public IPage<ExamAssignment> selectAssignmentPage(Integer pageNum, Integer pageSize, ExamAssignment assignment) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamAssignment> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectAssignmentPage(page, assignment);
    }

    @Override
    public ExamAssignment selectAssignmentById(Long assignmentId) {
        ExamAssignment assignment = baseMapper.selectAssignmentById(assignmentId);
        if (assignment != null) {
            // 查询关联的题目
            List<ExamQuestionBank> questions = assignmentQuestionMapper.selectQuestionsByAssignmentId(assignmentId);
            assignment.setQuestions(questions);
        }
        return assignment;
    }

    @Override
    public AssignmentVO selectAssignmentVOById(Long assignmentId) {
        ExamAssignment assignment = this.selectAssignmentById(assignmentId);

        // 获取课程id
        Long courseId = assignment.getCourseId();
        // 根据课程id 获取当前课程的总人数
        QueryWrapper<EduCourseStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        Long totalCount = eduCourseStudentMapper.selectCount(queryWrapper);
        // 获取提交人数
        QueryWrapper<ExamAssignmentSubmission> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("assignment_id",  assignmentId);

        Long submittedCount = submissionMapper.selectCount(queryWrapper1);
        if (assignment == null) {
            return null;
        }
        return convertToVO(assignment, totalCount, submittedCount);
    }

    @Override
    public IPage<ExamAssignment> selectStudentAssignmentPage(Integer pageNum, Integer pageSize, Long studentId, ExamAssignment assignment) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamAssignment> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectStudentAssignmentPage(page, studentId, assignment);
    }

    @Override
    public AssignmentVO selectStudentAssignmentVOById(Long assignmentId, Long studentId) {
        // 先获取作业基本信息
        AssignmentVO assignmentVO = this.selectAssignmentVOById(assignmentId);
        if (assignmentVO == null) {
            return null;
        }
        
        // 查询学生的提交信息
        ExamAssignmentSubmission submission = submissionMapper.selectByStudentAndAssignment(studentId, assignmentId);
        if (submission != null) {
            // 转换成VO
            SubmissionVO submissionVO = submissionService.convertToVO(submission);
            
            // 获取学生的答案
            List<AssignmentQuestionVO> answers = answerService.selectAnswerVOsBySubmissionId(submission.getSubmissionId());
            if (answers != null && !answers.isEmpty()) {
                // 将答案信息填充到题目中
                for (AssignmentQuestionVO question : assignmentVO.getQuestions()) {
                    for (AssignmentQuestionVO answer : answers) {
                        if (question.getQuestionId().equals(answer.getQuestionId())) {
                            question.setStudentAnswer(answer.getStudentAnswer());
                            question.setScore(answer.getScore());
                            question.setFeedback(answer.getFeedback());
                            break;
                        }
                    }
                }
            }
            
            assignmentVO.setSubmission(submissionVO);
        }
        
        return assignmentVO;
    }

    @Override
    @Transactional
    public boolean insertAssignment(AssignmentParam assignmentParam) {
        // 转换成实体
        ExamAssignment assignment = new ExamAssignment();
        BeanUtils.copyProperties(assignmentParam, assignment);
        
        // 设置默认状态
        if (assignment.getStatus() == null) {
            assignment.setStatus(QuestionConstants.STATUS_NORMAL);
        }
        
        // 设置默认删除标志
        assignment.setDelFlag(QuestionConstants.DEL_FLAG_NORMAL);
        
        // 保存作业基本信息
        boolean result = save(assignment);
        if (!result) {
            return false;
        }
        
        // 保存作业题目关联
        List<ExamAssignmentQuestion> assignmentQuestions = new ArrayList<>();
        BigDecimal totalPoints = BigDecimal.ZERO;
        
        // 处理直接指定的题目列表
        if (!CollectionUtils.isEmpty(assignmentParam.getQuestions())) {
            // 遍历题目参数
            for (int i = 0; i < assignmentParam.getQuestions().size(); i++) {
                AssignmentQuestionParam.QuestionDetail questionDetail = assignmentParam.getQuestions().get(i);
                
                ExamAssignmentQuestion assignmentQuestion = new ExamAssignmentQuestion();
                assignmentQuestion.setAssignmentId(assignment.getAssignmentId());
                assignmentQuestion.setQuestionId(questionDetail.getQuestionId());
                assignmentQuestion.setQuestionOrder(questionDetail.getQuestionOrder() != null ? questionDetail.getQuestionOrder() : i + 1);
                assignmentQuestion.setPoints(questionDetail.getPoints());
                
                assignmentQuestions.add(assignmentQuestion);
                
                // 累加总分
                if (questionDetail.getPoints() != null) {
                    totalPoints = totalPoints.add(questionDetail.getPoints());
                }
            }
        }
        
        // 处理题库ID列表
        if (!CollectionUtils.isEmpty(assignmentParam.getQuestionIds())) {
            // 获取当前已添加的题目ID
            List<Long> existingQuestionIds = assignmentQuestions.stream()
                    .map(ExamAssignmentQuestion::getQuestionId)
                    .collect(Collectors.toList());
            
            // 按照顺序添加
            int orderStart = assignmentQuestions.size() + 1;
            
            for (int i = 0; i < assignmentParam.getQuestionIds().size(); i++) {
                Long questionId = assignmentParam.getQuestionIds().get(i);
                
                // 避免重复添加题目
                if (existingQuestionIds.contains(questionId)) {
                    continue;
                }
                
                // 从题库中查询题目信息
                ExamQuestionBank question = questionBankMapper.selectById(questionId);
                if (question == null) {
                    continue;
                }
                
                // 创建关联
                ExamAssignmentQuestion assignmentQuestion = new ExamAssignmentQuestion();
                assignmentQuestion.setAssignmentId(assignment.getAssignmentId());
                assignmentQuestion.setQuestionId(questionId);
                assignmentQuestion.setQuestionOrder(orderStart + i);
                
                // 默认每题5分
                BigDecimal defaultPoints = BigDecimal.valueOf(5);
                assignmentQuestion.setPoints(defaultPoints);
                
                assignmentQuestions.add(assignmentQuestion);
                
                // 累加总分
                totalPoints = totalPoints.add(defaultPoints);
                
                // 添加到已处理列表，避免重复
                existingQuestionIds.add(questionId);
            }
        }
        
        // 批量插入作业题目关联
        if (!assignmentQuestions.isEmpty()) {
            assignmentQuestionMapper.batchInsert(assignmentQuestions);
            
            // 更新总分
            if (totalPoints.compareTo(BigDecimal.ZERO) > 0) {
                assignment.setTotalPoints(totalPoints);
                updateById(assignment);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean updateAssignment(AssignmentParam assignmentParam) {
        if (assignmentParam.getAssignmentId() == null) {
            return false;
        }
        
        // 转换成实体
        ExamAssignment assignment = new ExamAssignment();
        BeanUtils.copyProperties(assignmentParam, assignment);
        
        // 保存作业基本信息
        boolean result = updateById(assignment);
        if (!result) {
            return false;
        }
        
        Long assignmentId = assignment.getAssignmentId();
        
        // 删除原有作业题目关联
        assignmentQuestionMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamAssignmentQuestion>()
                .eq(ExamAssignmentQuestion::getAssignmentId, assignmentId));
        
        // 保存作业题目关联
        List<ExamAssignmentQuestion> assignmentQuestions = new ArrayList<>();
        BigDecimal totalPoints = BigDecimal.ZERO;
        
        // 处理直接指定的题目列表
        if (!CollectionUtils.isEmpty(assignmentParam.getQuestions())) {
            // 遍历题目参数
            for (int i = 0; i < assignmentParam.getQuestions().size(); i++) {
                AssignmentQuestionParam.QuestionDetail questionDetail = assignmentParam.getQuestions().get(i);
                
                ExamAssignmentQuestion assignmentQuestion = new ExamAssignmentQuestion();
                assignmentQuestion.setAssignmentId(assignmentId);
                assignmentQuestion.setQuestionId(questionDetail.getQuestionId());
                assignmentQuestion.setQuestionOrder(questionDetail.getQuestionOrder() != null ? questionDetail.getQuestionOrder() : i + 1);
                assignmentQuestion.setPoints(questionDetail.getPoints());
                
                assignmentQuestions.add(assignmentQuestion);
                
                // 累加总分
                if (questionDetail.getPoints() != null) {
                    totalPoints = totalPoints.add(questionDetail.getPoints());
                }
            }
        }
        
        // 处理题库ID列表
        if (!CollectionUtils.isEmpty(assignmentParam.getQuestionIds())) {
            // 获取当前已添加的题目ID
            List<Long> existingQuestionIds = assignmentQuestions.stream()
                    .map(ExamAssignmentQuestion::getQuestionId)
                    .collect(Collectors.toList());
            
            // 按照顺序添加
            int orderStart = assignmentQuestions.size() + 1;
            
            for (int i = 0; i < assignmentParam.getQuestionIds().size(); i++) {
                Long questionId = assignmentParam.getQuestionIds().get(i);
                
                // 避免重复添加题目
                if (existingQuestionIds.contains(questionId)) {
                    continue;
                }
                
                // 从题库中查询题目信息
                ExamQuestionBank question = questionBankMapper.selectById(questionId);
                if (question == null) {
                    continue;
                }
                
                // 创建关联
                ExamAssignmentQuestion assignmentQuestion = new ExamAssignmentQuestion();
                assignmentQuestion.setAssignmentId(assignmentId);
                assignmentQuestion.setQuestionId(questionId);
                assignmentQuestion.setQuestionOrder(orderStart + i);
                
                // 默认每题5分
                BigDecimal defaultPoints = BigDecimal.valueOf(5);
                assignmentQuestion.setPoints(defaultPoints);
                
                assignmentQuestions.add(assignmentQuestion);
                
                // 累加总分
                totalPoints = totalPoints.add(defaultPoints);
                
                // 添加到已处理列表，避免重复
                existingQuestionIds.add(questionId);
            }
        }
        
        // 批量插入作业题目关联
        if (!assignmentQuestions.isEmpty()) {
            assignmentQuestionMapper.batchInsert(assignmentQuestions);
            
            // 更新总分
            if (totalPoints.compareTo(BigDecimal.ZERO) > 0) {
                assignment.setTotalPoints(totalPoints);
                updateById(assignment);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAssignmentById(Long assignmentId) {
        // 删除作业题目关联
        assignmentQuestionMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamAssignmentQuestion>()
                .eq(ExamAssignmentQuestion::getAssignmentId, assignmentId));
        
        // 删除作业
        return removeById(assignmentId);
    }

    @Override
    @Transactional
    public boolean deleteAssignmentByIds(Long[] assignmentIds) {
        for (Long assignmentId : assignmentIds) {
            deleteAssignmentById(assignmentId);
        }
        return true;
    }
    
    /**
     * 将作业实体转换为VO
     *
     * @param assignment 作业实体
     * @return 作业VO
     */
    private AssignmentVO convertToVO(ExamAssignment assignment, Long totalCount, Long submittedCount) {
        if (assignment == null) {
            return null;
        }
        
        AssignmentVO vo = new AssignmentVO();
        BeanUtils.copyProperties(assignment, vo);
        
        // 处理题目
        if (!CollectionUtils.isEmpty(assignment.getQuestions())) {
            List<AssignmentQuestionVO> questionVOs = new ArrayList<>();
            
            // 查询题目与作业的关联信息
            List<ExamAssignmentQuestion> assignmentQuestions = assignmentQuestionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamAssignmentQuestion>()
                    .eq(ExamAssignmentQuestion::getAssignmentId, assignment.getAssignmentId())
                    .orderByAsc(ExamAssignmentQuestion::getQuestionOrder));
            
            for (ExamAssignmentQuestion assignmentQuestion : assignmentQuestions) {
                AssignmentQuestionVO questionVO = new AssignmentQuestionVO();
                questionVO.setAssignmentId(assignmentQuestion.getAssignmentId());
                questionVO.setQuestionId(assignmentQuestion.getQuestionId());
                questionVO.setQuestionOrder(assignmentQuestion.getQuestionOrder());
                questionVO.setPoints(assignmentQuestion.getPoints());
                
                // 查找对应的题目信息
                for (ExamQuestionBank question : assignment.getQuestions()) {
                    if (question.getQuestionId().equals(assignmentQuestion.getQuestionId())) {
                        // 转换成QuestionVO
                        QuestionVO questionDetailVO = new QuestionVO();
                        BeanUtils.copyProperties(question, questionDetailVO);
                        questionVO.setQuestion(questionDetailVO);
                        break;
                    }
                }
                
                questionVOs.add(questionVO);
            }
            
            vo.setQuestions(questionVOs);
        }
        vo.setTotalCount(Math.toIntExact(totalCount));
        vo.setSubmittedCount(Math.toIntExact(submittedCount));
        return vo;
    }

    @Override
    @Transactional
    public boolean associateQuestions(AssignmentQuestionParam param) {
        Long assignmentId = param.getAssignmentId();
        
        // 检查作业是否存在
        ExamAssignment assignment = baseMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new RuntimeException("作业不存在");
        }
        
        // 删除原有作业题目关联
        assignmentQuestionMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamAssignmentQuestion>()
                .eq(ExamAssignmentQuestion::getAssignmentId, assignmentId));
        
        // 如果没有题目列表，直接返回成功
        if (CollectionUtils.isEmpty(param.getQuestions())) {
            return true;
        }
        
        // 创建关联列表
        List<ExamAssignmentQuestion> assignmentQuestions = new ArrayList<>();
        BigDecimal totalPoints = BigDecimal.ZERO;
        
        // 处理题目列表
        for (int i = 0; i < param.getQuestions().size(); i++) {
            AssignmentQuestionParam.QuestionDetail questionDetail = param.getQuestions().get(i);
            
            // 从题库中查询题目信息，确保题目存在
            ExamQuestionBank question = questionBankMapper.selectById(questionDetail.getQuestionId());
            if (question == null) {
                continue;
            }
            
            // 创建关联
            ExamAssignmentQuestion assignmentQuestion = new ExamAssignmentQuestion();
            assignmentQuestion.setAssignmentId(assignmentId);
            assignmentQuestion.setQuestionId(questionDetail.getQuestionId());
            
            // 设置题目顺序
            assignmentQuestion.setQuestionOrder(
                questionDetail.getQuestionOrder() != null ? questionDetail.getQuestionOrder() : i + 1);
            
            // 设置分值，如果未提供则默认为5分
            BigDecimal points = questionDetail.getPoints();
            if (points == null || points.compareTo(BigDecimal.ZERO) <= 0) {
                points = BigDecimal.valueOf(5);
            }
            assignmentQuestion.setPoints(points);
            
            assignmentQuestions.add(assignmentQuestion);
            
            // 累加总分
            totalPoints = totalPoints.add(points);
        }
        
        // 批量插入作业题目关联
        if (!assignmentQuestions.isEmpty()) {
            assignmentQuestionMapper.batchInsert(assignmentQuestions);
            
            // 更新总分
            if (totalPoints.compareTo(BigDecimal.ZERO) > 0) {
                assignment.setTotalPoints(totalPoints);
                updateById(assignment);
            }
        }
        
        return true;
    }
} 