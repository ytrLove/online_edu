package com.exam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.mapper.ExamAssignmentAnswerMapper;
import com.exam.mapper.ExamAssignmentSubmissionMapper;
import com.exam.model.constant.ExamConstants;
import com.exam.model.entity.ExamAssignmentAnswer;
import com.exam.model.entity.ExamAssignmentSubmission;
import com.exam.model.param.AnswerParam;
import com.exam.model.param.SubmissionParam;
import com.exam.model.vo.AssignmentQuestionVO;
import com.exam.model.vo.QuestionVO;
import com.exam.model.vo.SubmissionVO;
import com.exam.service.ExamAssignmentAnswerService;
import com.exam.service.ExamAssignmentSubmissionService;
import com.util.CheckRole;
import com.util.GetId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业提交服务实现类
 */
@Service
public class ExamAssignmentSubmissionServiceImpl extends ServiceImpl<ExamAssignmentSubmissionMapper, ExamAssignmentSubmission> implements ExamAssignmentSubmissionService {

    @Autowired
    private ExamAssignmentAnswerService answerService;
    
    @Autowired
    private ExamAssignmentAnswerMapper answerMapper;

    @Autowired
    private CheckRole checkRole;

    @Autowired
    private GetId getId;
    
    @Override
    public IPage<ExamAssignmentSubmission> selectSubmissionPage(Integer pageNum, Integer pageSize, ExamAssignmentSubmission submission) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamAssignmentSubmission> page = new Page<>(pageNum, pageSize);

        // 检查用户角色
        if (checkRole.isTeacher()) {
            // 如果是教师，只返回当前教师教授的课程下的提交列表
            Long teacherId = getId.getId();
            if (teacherId != null) {
                return baseMapper.selectTeacherSubmissionPage(page, submission, teacherId);
            }
        } else if (checkRole.isAdmin()) {
            // 如果是管理员，返回所有提交列表
        return baseMapper.selectSubmissionPage(page, submission);
        }
        
        // 其他角色或无权限时返回空列表
        return new Page<>();
    }

    @Override
    public ExamAssignmentSubmission selectSubmissionById(Long submissionId) {
        ExamAssignmentSubmission submission = baseMapper.selectSubmissionById(submissionId);
        if (submission != null) {
            // 查询关联的答案
            List<ExamAssignmentAnswer> answers = answerService.selectAnswersBySubmissionId(submissionId);
            submission.setAnswers(answers);

        }
        return submission;
    }

    @Override
    public SubmissionVO selectSubmissionVOById(Long submissionId) {
        ExamAssignmentSubmission submission = this.selectSubmissionById(submissionId);
        if (submission == null) {
            return null;
        }
        return convertToVO(submission);
    }

    @Override
    public ExamAssignmentSubmission selectByStudentAndAssignment(Long studentId, Long assignmentId) {
        return baseMapper.selectByStudentAndAssignment(studentId, assignmentId);
    }

    @Override
    public List<ExamAssignmentSubmission> selectSubmissionStatsByAssignmentId(Long assignmentId) {
        return baseMapper.selectSubmissionStatsByAssignmentId(assignmentId);
    }

    @Override
    @Transactional
    public boolean submitAssignment(SubmissionParam submissionParam, Long studentId) {
        Long assignmentId = submissionParam.getAssignmentId();
        
        // 查询是否已提交
        ExamAssignmentSubmission existingSubmission = baseMapper.selectByStudentAndAssignment(studentId, assignmentId);
        
        ExamAssignmentSubmission submission;
        boolean isNewSubmission = false;
        
        if (existingSubmission == null) {
            // 如果不存在则创建新的提交记录
            submission = new ExamAssignmentSubmission();
            submission.setAssignmentId(assignmentId);
            submission.setStudentId(studentId);
            submission.setSubmissionTime(LocalDateTime.now());
            submission.setStatus(ExamConstants.SUBMISSION_STATUS_SUBMITTED);
            isNewSubmission = true;
        } else {
            // 如果已存在则更新
            submission = existingSubmission;
            submission.setSubmissionTime(LocalDateTime.now());
            submission.setStatus(ExamConstants.SUBMISSION_STATUS_SUBMITTED);
        }
        
        // 保存提交记录
        boolean result;
        if (isNewSubmission) {
            result = save(submission);
        } else {
            result = updateById(submission);
            // 删除原有答案
            answerService.deleteAnswersBySubmissionId(submission.getSubmissionId());
        }
        
        if (!result) {
            return false;
        }
        
        // 批量保存答案
        if (!CollectionUtils.isEmpty(submissionParam.getAnswers())) {
            return answerService.batchSaveAnswers(submission.getSubmissionId(), submissionParam.getAnswers());
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean gradeSubmission(SubmissionParam submissionParam) {
        Long submissionId = submissionParam.getSubmissionId();
        
        // 查询提交记录
        ExamAssignmentSubmission submission = getById(submissionId);
        if (submission == null) {
            return false;
        }
        
        // 更新批改状态
        submission.setStatus(ExamConstants.SUBMISSION_STATUS_GRADED);
        submission.setUpdateTime(LocalDateTime.now());
        
        // 计算总分
        if (!CollectionUtils.isEmpty(submissionParam.getAnswers())) {
            BigDecimal totalScore = BigDecimal.ZERO;
            for (AnswerParam answerParam : submissionParam.getAnswers()) {
                if (answerParam.getScore() != null) {
                    totalScore = totalScore.add(answerParam.getScore());
                }
            }
            submission.setTotalScore(totalScore);
        }
        
        // 设置反馈 - SubmissionParam可能没有feedback字段，根据实际情况处理
        // 如果SubmissionParam类没有此字段，可以暂时注释掉或移除此行
        // submission.setFeedback(submissionParam.getFeedback());
        
        // 更新提交记录
        boolean result = updateById(submission);
        if (!result) {
            return false;
        }
        
        // 批量更新答案分数
        if (!CollectionUtils.isEmpty(submissionParam.getAnswers())) {
            return answerService.batchUpdateScore(submissionId, submissionParam.getAnswers());
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean deleteSubmissionById(Long submissionId) {
        // 删除答案
        answerService.deleteAnswersBySubmissionId(submissionId);
        // 删除提交记录
        return removeById(submissionId);
    }

    @Override
    @Transactional
    public boolean deleteSubmissionByIds(Long[] submissionIds) {
        for (Long submissionId : submissionIds) {
            deleteSubmissionById(submissionId);
        }
        return true;
    }

    @Override
    public SubmissionVO convertToVO(ExamAssignmentSubmission submission) {
        if (submission == null) {
            return null;
        }
        
        SubmissionVO vo = new SubmissionVO();
        BeanUtils.copyProperties(submission, vo);
        
        // 处理答案
        if (!CollectionUtils.isEmpty(submission.getAnswers())) {
            List<AssignmentQuestionVO> answerVOs = new ArrayList<>();
            for (ExamAssignmentAnswer answer : submission.getAnswers()) {
                AssignmentQuestionVO answerVO = new AssignmentQuestionVO();
                BeanUtils.copyProperties(answer, answerVO);
                
                // 创建题目信息对象
                if (answerVO.getQuestion() == null && answer.getQuestionContent() != null) {
                    QuestionVO questionVO = new QuestionVO();
                    questionVO.setQuestionId(answer.getQuestionId());
                    questionVO.setQuestionContent(answer.getQuestionContent());
                    questionVO.setCorrectAnswer(answer.getCorrectAnswer());
                    questionVO.setTypeName(answer.getTypeName());
                    questionVO.setOptions(answer.getOptions());
                    answerVO.setQuestion(questionVO);
                }
                
                // 确保顺序信息存在
                if (answerVO.getQuestionOrder() == null) {
                    // 如果没有顺序信息，使用列表索引作为顺序
                    answerVO.setQuestionOrder(answerVOs.size() + 1);
                }
                
                // 确保答案分数信息存在
                if (answerVO.getScore() == null) {
                    answerVO.setScore(BigDecimal.ZERO);
                }
                
                // 确保反馈信息存在
                if (answerVO.getFeedback() == null) {
                    answerVO.setFeedback("");
                }
                
                answerVOs.add(answerVO);
            }
            vo.setAnswers(answerVOs);
        }
        
        // 确保一些基本属性不为空
        if (vo.getCreateTime() == null) {
            vo.setCreateTime(LocalDateTime.now());
        }
        
        if (vo.getTotalScore() == null) {
            vo.setTotalScore(BigDecimal.ZERO);
        }
        
        if (vo.getFeedback() == null) {
            vo.setFeedback("");
        }
        
        if (vo.getUpdateTime() == null && ExamConstants.SUBMISSION_STATUS_GRADED.equals(vo.getStatus())) {
            vo.setUpdateTime(LocalDateTime.now());
        }
        
        return vo;
    }
} 