package com.exam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.model.constant.ExamConstants;
import com.exam.model.constant.QuestionConstants;
import com.exam.mapper.ExamPaperMapper;
import com.exam.mapper.ExamPaperQuestionMapper;
import com.exam.mapper.ExamRecordMapper;
import com.exam.model.entity.ExamPaper;
import com.exam.model.entity.ExamPaperQuestion;
import com.exam.model.entity.ExamQuestionBank;
import com.exam.model.entity.ExamRecord;
import com.exam.model.param.ExamPaperParam;
import com.exam.model.param.ExamQuestionParam;
import com.exam.model.vo.ExamPaperVO;
import com.exam.model.vo.ExamQuestionVO;
import com.exam.service.ExamPaperService;
import com.exam.service.ExamQuestionBankService;
import com.exam.service.ExamRecordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 考试试卷服务实现类
 */
@Service
public class ExamPaperServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements ExamPaperService {

    @Autowired
    private ExamPaperQuestionMapper paperQuestionMapper;
    
    @Autowired
    private ExamRecordMapper recordMapper;
    
    @Autowired
    private ExamRecordService recordService;

    @Autowired
    private ExamQuestionBankService questionBankService;
    
    @Override
    public IPage<ExamPaper> selectExamPage(Integer pageNum, Integer pageSize, ExamPaper exam) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamPaper> page = new Page<>(pageNum, pageSize);
        IPage<ExamPaper> resultPage = baseMapper.selectExamPage(page, exam);
        

        // 但如果需要对题目集合进行处理，可以在这里处理
        return resultPage;
    }

    @Override
    public ExamPaper selectExamById(Long examId) {
        ExamPaper exam = baseMapper.selectExamById(examId);
        if (exam != null) {
            // 查询关联的题目
            List<ExamQuestionBank> questions = paperQuestionMapper.selectQuestionsByExamId(examId);
            exam.setQuestions(questions);
        }
        return exam;
    }

    @Override
    public ExamPaperVO selectExamVOById(Long examId) {
        ExamPaper exam = this.selectExamById(examId);
        if (exam == null) {
            return null;
        }
        return convertToVO(exam);
    }

    @Override
    public IPage<ExamPaper> selectStudentExamPage(Integer pageNum, Integer pageSize, Long studentId, ExamPaper exam) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamPaper> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectStudentExamPage(page, studentId, exam);
    }

    @Override
    public ExamPaperVO selectStudentExamVOById(Long examId, Long studentId) {
        // 先获取试卷基本信息
        ExamPaperVO examVO = this.selectExamVOById(examId);
        if (examVO == null) {
            return null;
        }
        
        // 检查考试状态
        LocalDateTime now = LocalDateTime.now();
        // 考试未开始时也可以查看考试信息，但标记状态为未开始
        if (examVO.getStartTime() != null && now.isBefore(examVO.getStartTime())) {
            // 考试未开始，但允许查看信息
            examVO.setStatus(QuestionConstants.EXAM_STATUS_NOT_STARTED);
            // 不返回null，允许学生提前查看考试信息
            // return examVO;
        }
        
        if (examVO.getEndTime() != null && now.isAfter(examVO.getEndTime())) {
            // 考试已结束
            examVO.setStatus(QuestionConstants.EXAM_STATUS_ENDED);
        }
        
        // 查询学生的考试记录
        ExamRecord record = recordMapper.selectStudentRecordByExamId(examId, studentId);
        if (record != null) {
            // 设置考试记录相关信息
            examVO.setRecordId(record.getRecordId());
            examVO.setStartExamTime(record.getStartTime());
            examVO.setEndExamTime(record.getSubmitTime());
            examVO.setStudentScore(record.getTotalScore());
            examVO.setSubmitted(ExamConstants.EXAM_STATUS_SUBMITTED.equals(record.getStatus()) || 
                              ExamConstants.EXAM_STATUS_GRADED.equals(record.getStatus()));
            examVO.setGraded(ExamConstants.EXAM_STATUS_GRADED.equals(record.getStatus()));
        }
        
        return examVO;
    }

    @Override
    @Transactional
    public boolean insertExam(ExamPaperParam examParam) {
        // 转换成实体
        ExamPaper exam = new ExamPaper();
        BeanUtils.copyProperties(examParam, exam);
        
        // 设置默认状态
        if (exam.getStatus() == null) {
            exam.setStatus(QuestionConstants.STATUS_NORMAL);
        }
        
        // 设置默认删除标志
        exam.setDelFlag(QuestionConstants.DEL_FLAG_NORMAL);

        // 设置总分为0
        exam.setTotalPoints(BigDecimal.ZERO);

        exam.setCreateTime(LocalDateTime.now());
        exam.setUpdateTime(LocalDateTime.now());
        // 保存试卷基本信息
        return save(exam);
    }

    @Override
    @Transactional
    public boolean updateExam(ExamPaperParam examParam) {
        if (examParam.getExamId() == null) {
            return false;
        }
        
        // 检查是否已有学生参加考试
        int count = recordMapper.countByExamId(examParam.getExamId());
        if (count > 0) {
            // 已有学生参加考试，不允许修改
            return false;
        }
        
        // 转换成实体
        ExamPaper exam = new ExamPaper();
        BeanUtils.copyProperties(examParam, exam);
        
        // 保存试卷基本信息
        boolean result = updateById(exam);
        if (!result) {
            return false;
        }
        
        Long examId = exam.getExamId();
        
        // 删除原有试卷题目关联
        paperQuestionMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getExamId, examId));
        
        // 保存试卷题目关联
        if (!CollectionUtils.isEmpty(examParam.getQuestions())) {
            List<ExamPaperQuestion> paperQuestions = new ArrayList<>();
            
            // 计算总分
            BigDecimal totalPoints = BigDecimal.ZERO;
            
            // 遍历题目参数
            for (int i = 0; i < examParam.getQuestions().size(); i++) {
                ExamQuestionParam questionParam = examParam.getQuestions().get(i);
                
                ExamPaperQuestion paperQuestion = new ExamPaperQuestion();
                paperQuestion.setExamId(examId);
                // 判断是个有排序
                if (questionParam.getQuestionOrder() != null) {
                    // 获取排序
                    paperQuestion.setQuestionOrder(questionParam.getQuestionOrder());
                }
                paperQuestion.setQuestionOrder(questionParam.getQuestionOrder() != null ? questionParam.getQuestionOrder() : i + 1);
                paperQuestion.setPoints(questionParam.getPoints());
                
                paperQuestions.add(paperQuestion);
                
                // 累加总分
                if (questionParam.getPoints() != null) {
                    totalPoints = totalPoints.add(questionParam.getPoints());
                }
            }
            
            // 批量插入试卷题目关联
            paperQuestionMapper.batchInsert(paperQuestions);
            
            // 更新总分
            if (totalPoints.compareTo(BigDecimal.ZERO) > 0) {
                exam.setTotalPoints(totalPoints);
                updateById(exam);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean deleteExamById(Long examId) {
        // 检查是否已有学生参加考试
        int count = recordMapper.countByExamId(examId);
        if (count > 0) {
            // 已有学生参加考试，不允许删除
            return false;
        }
        
        // 删除试卷题目关联
        paperQuestionMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getExamId, examId));
        
        // 删除试卷
        return removeById(examId);
    }

    @Override
    @Transactional
    public boolean deleteExamByIds(Long[] examIds) {
        for (Long examId : examIds) {
            if (!deleteExamById(examId)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public List<ExamQuestionVO> selectExamQuestionList(Long examId) {
        // 先获取试卷详情
        ExamPaper exam = this.selectExamById(examId);
        if (exam == null) {
            return new ArrayList<>();
        }
        
        List<ExamQuestionVO> questionVOs = new ArrayList<>();
        
        // 查询题目与试卷的关联信息
        List<ExamPaperQuestion> paperQuestions = paperQuestionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getExamId, examId)
                .orderByAsc(ExamPaperQuestion::getQuestionOrder));
        
        for (ExamPaperQuestion paperQuestion : paperQuestions) {
            ExamQuestionVO questionVO = new ExamQuestionVO();
            questionVO.setExamId(paperQuestion.getExamId());
            questionVO.setQuestionId(paperQuestion.getQuestionId());
            questionVO.setQuestionOrder(paperQuestion.getQuestionOrder());
            questionVO.setPoints(paperQuestion.getPoints());
            
            // 查找对应的题目信息
            for (ExamQuestionBank question : exam.getQuestions()) {
                if (question.getQuestionId().equals(paperQuestion.getQuestionId())) {
                    // 正确映射字段名称
                    questionVO.setQuestionTypeId(question.getQuestionTypeId());
                    questionVO.setTypeName(question.getTypeName());
                    // 将questionContent映射到content
                    questionVO.setContent(question.getQuestionContent());
                    // 设置题目名称
                    questionVO.setQuestionName(question.getQuestionName());
                    questionVO.setOptions(question.getOptions());
                    // 将correctAnswer映射到answer
                    questionVO.setAnswer(question.getCorrectAnswer());
                    questionVO.setAnalysis(question.getExplanation());
                    
                    // 根据题型设置是否自动评分
                    if (question.getTypeName() != null) {
                        boolean autoGraded = question.getTypeName().equals("单选题") || 
                                            question.getTypeName().equals("多选题") || 
                                            question.getTypeName().equals("判断题");
                        questionVO.setIsAutoGraded(autoGraded);
                    }
                    
                    break;
                }
            }
            
            questionVOs.add(questionVO);
        }
        
        return questionVOs;
    }

    @Override
    public boolean updateExamQuestions(Long examId, Long questionId, BigDecimal points) {
        return false;
    }

    @Override
    public boolean insertExamQuestions(List<ExamPaperQuestion> examParam) {
        AtomicInteger createdPoints = new AtomicInteger();
        examParam.forEach(param -> {
            ExamPaperQuestion paperQuestion = new ExamPaperQuestion();
            paperQuestion.setExamId(param.getExamId());
            paperQuestion.setQuestionId(param.getQuestionId());
            paperQuestion.setQuestionOrder(param.getQuestionOrder());
            paperQuestion.setPoints(param.getPoints());

             // 判断题目是否已存在
            if (paperQuestionMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                    .eq(ExamPaperQuestion::getExamId, param.getExamId())
                    .eq(ExamPaperQuestion::getQuestionId, param.getQuestionId())) != null) {
                return;
            }
            // 插入试卷题目关联
            paperQuestionMapper.insert(paperQuestion);
            createdPoints.addAndGet(param.getPoints().intValue());
        });

        // 更新试卷总分
        ExamPaper exam = this.selectExamById(examParam.get(0).getExamId());
        exam.setTotalPoints(exam.getTotalPoints().add(BigDecimal.valueOf(createdPoints.get())));
        this.updateById(exam);
        return true;
    }

    @Override
    public boolean deleteExamQuestion(Long examId, Long questionId) {
        // 获取题目
        ExamPaperQuestion paperQuestion = paperQuestionMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getExamId, examId)
                .eq(ExamPaperQuestion::getQuestionId, questionId));
        if (paperQuestionMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                .eq(ExamPaperQuestion::getExamId, examId)
                .eq(ExamPaperQuestion::getQuestionId, questionId)) > 0) {
            // 更新试卷总分
            ExamPaper exam = this.selectExamById(examId);
            exam.setTotalPoints(exam.getTotalPoints().subtract(paperQuestion.getPoints()));

            return this.updateById(exam);
                }

        return false;
    }

    @Override
    public boolean unpublishExam(Long examId) {
        if (this.update(
            new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ExamPaper>()
                .set(ExamPaper::getStatus, 0)
                .eq(ExamPaper::getExamId, examId))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean publishExam(Long examId) {
        if (this.update(
            new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ExamPaper>()
                .set(ExamPaper::getStatus, 1)
                .eq(ExamPaper::getExamId, examId))) {
            return true;
        }
        return false;
    }


    /**
     * 将试卷实体转换为VO
     *
     * @param exam 试卷实体
     * @return 试卷VO
     */
    private ExamPaperVO convertToVO(ExamPaper exam) {
        if (exam == null) {
            return null;
        }
        
        ExamPaperVO vo = new ExamPaperVO();
        BeanUtils.copyProperties(exam, vo);
        
        // 处理题目
        if (!CollectionUtils.isEmpty(exam.getQuestions())) {
            List<ExamQuestionVO> questionVOs = new ArrayList<>();
            
            // 查询题目与试卷的关联信息
            List<ExamPaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamPaperQuestion>()
                    .eq(ExamPaperQuestion::getExamId, exam.getExamId())
                    .orderByAsc(ExamPaperQuestion::getQuestionOrder));
            
            for (ExamPaperQuestion paperQuestion : paperQuestions) {
                ExamQuestionVO questionVO = new ExamQuestionVO();
                questionVO.setExamId(paperQuestion.getExamId());
                questionVO.setQuestionId(paperQuestion.getQuestionId());
                questionVO.setQuestionOrder(paperQuestion.getQuestionOrder());
                questionVO.setPoints(paperQuestion.getPoints());
                
                // 查找对应的题目信息
                for (ExamQuestionBank question : exam.getQuestions()) {
                    if (question.getQuestionId().equals(paperQuestion.getQuestionId())) {
                        // 正确映射字段名称
                        questionVO.setQuestionTypeId(question.getQuestionTypeId());
                        questionVO.setTypeName(question.getTypeName());
                        // 将questionContent映射到content
                        questionVO.setContent(question.getQuestionContent());
                        // 设置题目名称
                        questionVO.setQuestionName(question.getQuestionName());
                        
                        // 处理选项
                        if (question.getOptions() != null && 
                            (question.getTypeName().equals("单选题") || 
                             question.getTypeName().equals("多选题") || 
                             question.getTypeName().equals("判断题"))) {
                            try {
                                // 将选项转换为对象格式
                                JsonNode optionsNode = new ObjectMapper().readTree(question.getOptions());
                                if (optionsNode.isArray()) {
                                    ObjectNode formattedOptions = new ObjectMapper().createObjectNode();
                                    for (JsonNode option : optionsNode) {
                                        String key = option.get("key").asText();
                                        String value = option.get("value").asText();
                                        formattedOptions.put(key, value);
                                    }
                                    questionVO.setOptions(new ObjectMapper().writeValueAsString(formattedOptions));
                                } else {
                                    questionVO.setOptions(question.getOptions());
                                }
                            } catch (Exception e) {
                                // 如果解析失败，使用原始选项
                                questionVO.setOptions(question.getOptions());
                            }
                        } else {
                            questionVO.setOptions(question.getOptions());
                        }
                        
                        // 将correctAnswer映射到answer
                        questionVO.setAnswer(question.getCorrectAnswer());
                        questionVO.setAnalysis(question.getExplanation());
                        
                        // 设置是否已回答（初始为false）
                        questionVO.setIsAnswered(false);
                        
                        // 根据题型设置是否自动评分
                        if (question.getTypeName() != null) {
                            boolean autoGraded = question.getTypeName().equals("单选题") || 
                                                question.getTypeName().equals("多选题") || 
                                                question.getTypeName().equals("判断题");
                            questionVO.setIsAutoGraded(autoGraded);
                        }
                        
                        break;
                    }
                }
                
                questionVOs.add(questionVO);
            }
            
            vo.setQuestions(questionVOs);
        }
        
        // 设置考试状态
        LocalDateTime now = LocalDateTime.now();
        if (vo.getStartTime() != null && now.isBefore(vo.getStartTime())) {
            vo.setStatus(QuestionConstants.EXAM_STATUS_NOT_STARTED);
        } else if (vo.getEndTime() != null && now.isAfter(vo.getEndTime())) {
            vo.setStatus(QuestionConstants.EXAM_STATUS_ENDED);
        } else {
            vo.setStatus(QuestionConstants.EXAM_STATUS_IN_PROGRESS);
        }
        
        return vo;
    }
} 