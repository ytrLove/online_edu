package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.mapper.ExamAnswerMapper;
import com.exam.mapper.ExamPaperQuestionMapper;
import com.exam.mapper.ExamQuestionBankMapper;
import com.exam.mapper.ExamRecordMapper;
import com.exam.model.entity.ExamAnswer;
import com.exam.model.entity.ExamPaperQuestion;
import com.exam.model.entity.ExamQuestionBank;
import com.exam.model.entity.ExamRecord;
import com.exam.model.param.AnswerParam;
import com.exam.model.vo.ExamQuestionVO;
import com.exam.service.ExamAnswerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试答案服务实现类
 */
@Service
public class ExamAnswerServiceImpl extends ServiceImpl<ExamAnswerMapper, ExamAnswer> implements ExamAnswerService {

    @Autowired
    private ExamQuestionBankMapper questionBankMapper;
    
    @Autowired
    private ExamPaperQuestionMapper paperQuestionMapper;
    
    @Autowired
    private ExamRecordMapper recordMapper;
    
    @Override
    public List<ExamAnswer> selectAnswersByRecordId(Long recordId) {
        return baseMapper.selectAnswersByRecordId(recordId);
    }

    @Override
    public List<ExamQuestionVO> selectAnswerVOsByRecordId(Long recordId) {
        // 获取答案列表
        List<ExamAnswer> answers = this.selectAnswersByRecordId(recordId);
        if (CollectionUtils.isEmpty(answers)) {
            return new ArrayList<>();
        }
        
        // 获取考试记录信息
        ExamRecord record = recordMapper.selectById(recordId);
        if (record == null) {
            return new ArrayList<>();
        }
        
        // 获取题目信息
        List<Long> questionIds = answers.stream()
                .map(ExamAnswer::getQuestionId)
                .collect(Collectors.toList());
        
        List<ExamQuestionBank> questions = questionBankMapper.selectBatchIds(questionIds);
        Map<Long, ExamQuestionBank> questionMap = questions.stream()
                .collect(Collectors.toMap(ExamQuestionBank::getQuestionId, q -> q));
        
        // 获取试卷题目关联信息（包含分数）
        LambdaQueryWrapper<ExamPaperQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPaperQuestion::getExamId, record.getExamId());
        List<ExamPaperQuestion> paperQuestions = paperQuestionMapper.selectList(wrapper);
        Map<Long, ExamPaperQuestion> paperQuestionMap = paperQuestions.stream()
                .collect(Collectors.toMap(ExamPaperQuestion::getQuestionId, q -> q));
        
        // 转换为VO对象
        return answers.stream().map(answer -> {
            ExamQuestionVO vo = new ExamQuestionVO();
            // 设置答案相关信息
            vo.setQuestionId(answer.getQuestionId());
            vo.setStudentAnswer(answer.getStudentAnswer());
            vo.setScore(answer.getScore());
            
            // 设置题目信息
            ExamQuestionBank question = questionMap.get(answer.getQuestionId());
            if (question != null) {
                vo.setQuestionTypeId(question.getQuestionTypeId());
                vo.setTypeName(question.getTypeName());
                vo.setContent(question.getQuestionContent());
                vo.setOptions(question.getOptions());
                vo.setAnswer(question.getCorrectAnswer());
                vo.setAnalysis(question.getExplanation());
                vo.setDifficulty(question.getDifficulty());
                vo.setIsAutoGraded(question.getIsAutoGraded());
            }
            
            // 设置分数信息
            ExamPaperQuestion paperQuestion = paperQuestionMap.get(answer.getQuestionId());
            if (paperQuestion != null) {
                    vo.setPoints(paperQuestion.getPoints());
                    vo.setQuestionOrder(paperQuestion.getQuestionOrder());
        }
        
            // 设置是否正确
            if (answer.getScore() != null && paperQuestion != null && paperQuestion.getPoints() != null) {
                vo.setIsCorrect(answer.getScore().compareTo(paperQuestion.getPoints()) == 0);
            }
            
            // 设置是否已答题
            vo.setIsAnswered(answer.getStudentAnswer() != null && !answer.getStudentAnswer().trim().isEmpty());
            
            return vo;
        }).sorted(Comparator.comparing(ExamQuestionVO::getQuestionOrder)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean batchSaveAnswers(Long recordId, List<AnswerParam> answers) {
        if (CollectionUtils.isEmpty(answers)) {
            return true;
        }
        
        // 先删除已有答案
        deleteAnswersByRecordId(recordId);
        
        // 获取所有题目信息用于自动评分
        List<Long> questionIds = answers.stream()
                .map(AnswerParam::getQuestionId)
                .collect(Collectors.toList());
        
        List<ExamQuestionBank> questions = questionBankMapper.selectBatchIds(questionIds);
        Map<Long, ExamQuestionBank> questionMap = questions.stream()
                .collect(Collectors.toMap(ExamQuestionBank::getQuestionId, q -> q));
        
        // 获取当前考试记录的试卷ID
        ExamRecord record = recordMapper.selectById(recordId);
        if (record == null) {
            return false;
        }
        Long examId = record.getExamId();
        
        // 获取题目分值信息，指定具体的examId来避免获取其他试卷的相同题目
        List<ExamPaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getExamId, examId)
                .in(ExamPaperQuestion::getQuestionId, questionIds));
        
        // 使用试卷ID+题目ID作为复合键，或者处理冲突的合并函数
        Map<Long, ExamPaperQuestion> paperQuestionMap = paperQuestions.stream()
                .collect(Collectors.toMap(
                    ExamPaperQuestion::getQuestionId, 
                    q -> q,
                    // 如果有重复键，保留第一个值
                    (existing, replacement) -> existing
                ));
        
        // 转换为实体
        List<ExamAnswer> answerEntities = new ArrayList<>();
        for (AnswerParam param : answers) {
            ExamAnswer answer = new ExamAnswer();
            answer.setRecordId(recordId);
            answer.setQuestionId(param.getQuestionId());
            answer.setStudentAnswer(param.getStudentAnswer());
            
            // 自动评分逻辑
            ExamQuestionBank question = questionMap.get(param.getQuestionId());
            if (question != null && question.getIsAutoGraded() != null && question.getIsAutoGraded()) {
                ExamPaperQuestion paperQuestion = paperQuestionMap.get(param.getQuestionId());
                
                if (paperQuestion != null && paperQuestion.getPoints() != null) {
                    // 单选题、判断题直接比较答案
                    if (question.getTypeName().equals("单选题") || question.getTypeName().equals("判断题")) {
                        if (param.getStudentAnswer() != null && param.getStudentAnswer().equals(question.getCorrectAnswer())) {
                            answer.setScore(paperQuestion.getPoints());
                        } else {
                            answer.setScore(BigDecimal.ZERO);
                        }
                    } 
                    // 多选题需要比较所有选项
                    else if (question.getTypeName().equals("多选题")) {
                        // 多选题答案格式：A,B,C
                        if (param.getStudentAnswer() != null) {
                            // 排序答案以确保顺序一致再比较
                            String[] studentAnswerArr = param.getStudentAnswer().split(",");
                            Arrays.sort(studentAnswerArr);
                            String sortedStudentAnswer = String.join(",", studentAnswerArr);
                            
                            String[] correctAnswerArr = question.getCorrectAnswer().split(",");
                            Arrays.sort(correctAnswerArr);
                            String sortedCorrectAnswer = String.join(",", correctAnswerArr);
                            
                            if (sortedStudentAnswer.equals(sortedCorrectAnswer)) {
                                answer.setScore(paperQuestion.getPoints());
                            } else {
                                answer.setScore(BigDecimal.ZERO);
                            }
                        } else {
                            answer.setScore(BigDecimal.ZERO);
                        }
                    }
                    // 其他题型不自动评分
                    else {
                        answer.setScore(null);
                    }
                }
            }
            
            answerEntities.add(answer);
        }
        
        // 批量插入
        saveBatch(answerEntities);
        return true;
    }

    @Override
    @Transactional
    public boolean batchUpdateScore(Long recordId, List<AnswerParam> answers) {
        if (CollectionUtils.isEmpty(answers)) {
            return true;
        }
        
        // 转换为实体
        List<ExamAnswer> updatedAnswers = new ArrayList<>();
        for (AnswerParam param : answers) {
            if (param.getQuestionId() == null) {
                continue;
            }
            
            ExamAnswer answer = new ExamAnswer();
            answer.setRecordId(recordId);
            answer.setQuestionId(param.getQuestionId());
                answer.setScore(param.getScore());
                updatedAnswers.add(answer);
        }
        
        if (updatedAnswers.isEmpty()) {
            return true;
        }
        
        // 使用自定义的Mapper方法进行批量更新
        return baseMapper.batchUpdateScore(updatedAnswers) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAnswersByRecordId(Long recordId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamAnswer> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ExamAnswer::getRecordId, recordId);
        return remove(wrapper);
    }
} 