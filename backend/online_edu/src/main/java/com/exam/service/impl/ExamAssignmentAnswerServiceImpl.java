package com.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.mapper.ExamAssignmentAnswerMapper;
import com.exam.mapper.ExamQuestionBankMapper;
import com.exam.model.entity.ExamAssignmentAnswer;
import com.exam.model.entity.ExamQuestionBank;
import com.exam.model.param.AnswerParam;
import com.exam.model.vo.AssignmentQuestionVO;
import com.exam.model.vo.QuestionVO;
import com.exam.service.ExamAssignmentAnswerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 作业答案服务实现类
 */
@Service
public class ExamAssignmentAnswerServiceImpl extends ServiceImpl<ExamAssignmentAnswerMapper, ExamAssignmentAnswer> implements ExamAssignmentAnswerService {

    @Autowired
    private ExamQuestionBankMapper questionBankMapper;
    
    @Override
    public List<ExamAssignmentAnswer> selectAnswersBySubmissionId(Long submissionId) {
        return baseMapper.selectAnswersBySubmissionId(submissionId);
    }

    @Override
    public List<AssignmentQuestionVO> selectAnswerVOsBySubmissionId(Long submissionId) {
        // 获取答案列表
        List<ExamAssignmentAnswer> answers = this.selectAnswersBySubmissionId(submissionId);
        if (CollectionUtils.isEmpty(answers)) {
            return new ArrayList<>();
        }
        
        // 获取题目信息
        List<Long> questionIds = answers.stream()
                .map(ExamAssignmentAnswer::getQuestionId)
                .collect(Collectors.toList());
        
        List<ExamQuestionBank> questions = questionBankMapper.selectBatchIds(questionIds);
        Map<Long, ExamQuestionBank> questionMap = questions.stream()
                .collect(Collectors.toMap(ExamQuestionBank::getQuestionId, q -> q));
        
        // 构建VO对象
        List<AssignmentQuestionVO> result = new ArrayList<>();
        for (ExamAssignmentAnswer answer : answers) {
            AssignmentQuestionVO vo = new AssignmentQuestionVO();
            // 复制答案信息
            vo.setQuestionId(answer.getQuestionId());
            vo.setStudentAnswer(answer.getStudentAnswer());
            vo.setScore(answer.getScore());
            vo.setFeedback(answer.getFeedback());
            
            // 设置题目信息
            ExamQuestionBank question = questionMap.get(answer.getQuestionId());
            if (question != null) {
                // 通过QuestionVO设置题目信息
                QuestionVO questionVO = new QuestionVO();
                questionVO.setQuestionId(question.getQuestionId());
                questionVO.setQuestionTypeId(question.getQuestionTypeId());
                questionVO.setTypeName(question.getTypeName());
                questionVO.setIsAutoGraded(question.getIsAutoGraded());
                questionVO.setQuestionContent(question.getQuestionContent());
                questionVO.setOptions(question.getOptions());
                questionVO.setCorrectAnswer(question.getCorrectAnswer());
                questionVO.setExplanation(question.getExplanation());
                
                vo.setQuestion(questionVO);
            }
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean batchSaveAnswers(Long submissionId, List<AnswerParam> answers) {
        if (CollectionUtils.isEmpty(answers)) {
            return true;
        }
        
        // 转换为实体
        List<ExamAssignmentAnswer> answerEntities = new ArrayList<>();
        for (AnswerParam param : answers) {
            ExamAssignmentAnswer answer = new ExamAssignmentAnswer();
            answer.setSubmissionId(submissionId);
            answer.setQuestionId(param.getQuestionId());
            answer.setStudentAnswer(param.getStudentAnswer());
            
            // 对于可自动评分的题目，可以在这里进行评分逻辑
            // 这里简化处理，由教师批改
            
            answerEntities.add(answer);
        }
        
        // 批量插入
        int rows = baseMapper.batchInsert(answerEntities);
        return rows == answerEntities.size();
    }

    @Override
    @Transactional
    public boolean batchUpdateScore(Long submissionId, List<AnswerParam> answers) {
        if (CollectionUtils.isEmpty(answers)) {
            return true;
        }
        
        // 转换为实体
        List<ExamAssignmentAnswer> answerEntities = new ArrayList<>();
        for (AnswerParam param : answers) {
            if (param.getQuestionId() == null) {
                continue;
            }
            
            ExamAssignmentAnswer answer = new ExamAssignmentAnswer();
            answer.setSubmissionId(submissionId);
            answer.setQuestionId(param.getQuestionId());
            answer.setScore(param.getScore());
            answer.setFeedback(param.getFeedback());
            
            answerEntities.add(answer);
        }
        
        if (answerEntities.isEmpty()) {
            return true;
        }
        
        // 批量更新
        int rows = baseMapper.batchUpdateScore(answerEntities);
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean deleteAnswersBySubmissionId(Long submissionId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamAssignmentAnswer> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ExamAssignmentAnswer::getSubmissionId, submissionId);
        return remove(wrapper);
    }
} 