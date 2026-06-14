package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.mapper.ExamAssignmentQuestionMapper;
import com.exam.mapper.ExamQuestionBankMapper;
import com.exam.mapper.ExamQuestionTypeMapper;
import com.exam.model.constant.QuestionConstants;
import com.exam.model.entity.ExamAssignmentQuestion;
import com.exam.model.entity.ExamQuestionBank;
import com.exam.model.entity.ExamQuestionType;
import com.exam.model.param.QuestionParam;
import com.exam.model.vo.ExamQuestionVO;
import com.exam.model.vo.QuestionVO;
import com.exam.service.ExamQuestionBankService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 题库服务实现类
 */
@Service
public class ExamQuestionBankServiceImpl extends ServiceImpl<ExamQuestionBankMapper, ExamQuestionBank> implements ExamQuestionBankService {

    @Autowired
    private ExamQuestionTypeMapper questionTypeMapper;
    
    @Autowired
    private ExamAssignmentQuestionMapper assignmentQuestionMapper;

    @Override
    public IPage<ExamQuestionBank> selectQuestionPage(Integer pageNum, Integer pageSize, ExamQuestionBank question) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ExamQuestionBank> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectQuestionPage(page, question);
    }

    @Override
    public ExamQuestionBank selectQuestionById(Long questionId) {
        return baseMapper.selectQuestionById(questionId);
    }

    @Override
    public QuestionVO selectQuestionVOById(Long questionId) {
        ExamQuestionBank question = this.selectQuestionById(questionId);
        if (question == null) {
            return null;
        }
        return convertToVO(question);
    }

    @Override
    public List<ExamQuestionBank> selectQuestionByIds(List<Long> questionIds) {
        return baseMapper.selectQuestionByIds(questionIds);
    }

    @Override
    @Transactional
    public boolean insertQuestion(QuestionParam questionParam) {
        ExamQuestionBank question = new ExamQuestionBank();
        BeanUtils.copyProperties(questionParam, question);



        
        // 设置默认状态
        if (question.getStatus() == null) {
            question.setStatus(QuestionConstants.STATUS_NORMAL);
        }
        
        // 设置默认删除标志
        question.setDelFlag(QuestionConstants.DEL_FLAG_NORMAL);

        question.setCreateTime(LocalDateTime.now());
        // 获取题目类型，设置是否自动评分标志
        if (question.getQuestionTypeId() != null) {
            ExamQuestionType questionType = questionTypeMapper.selectById(question.getQuestionTypeId());
            if (questionType != null) {
                question.setTypeName(questionType.getTypeName());
                question.setIsAutoGraded(questionType.getIsAutoGraded() == 1);
            }
        }
        
        return save(question);
    }

    @Override
    @Transactional
    public boolean updateQuestion(QuestionParam questionParam) {
        if (questionParam.getQuestionId() == null) {
            return false;
        }
        
        ExamQuestionBank question = new ExamQuestionBank();
        BeanUtils.copyProperties(questionParam, question);
        
        // 获取题目类型，设置是否自动评分标志
        if (question.getQuestionTypeId() != null) {
            ExamQuestionType questionType = questionTypeMapper.selectById(question.getQuestionTypeId());
            if (questionType != null) {
                question.setTypeName(questionType.getTypeName());
                question.setIsAutoGraded(questionType.getIsAutoGraded() == 1);
            }
        }
        
        return updateById(question);
    }

    @Override
    public boolean deleteQuestionById(Long questionId) {
        return removeById(questionId);
    }

    @Override
    public boolean deleteQuestionByIds(Long[] questionIds) {
        List<Long> idList = Arrays.asList(questionIds);
        return removeByIds(idList);
    }

    @Override
    public List<ExamQuestionVO> getQuestionsByAssignmentId(Long assignmentId) {
        // 查询作业的题目关联
        LambdaQueryWrapper<ExamAssignmentQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamAssignmentQuestion::getAssignmentId, assignmentId)
               .orderByAsc(ExamAssignmentQuestion::getQuestionOrder);
        
        List<ExamAssignmentQuestion> questionRelations = assignmentQuestionMapper.selectList(wrapper);
        
        if (questionRelations == null || questionRelations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取题目ID
        List<Long> questionIds = questionRelations.stream()
                .map(ExamAssignmentQuestion::getQuestionId)
                .collect(Collectors.toList());
        
        // 查询题目详情
        List<ExamQuestionBank> questions = baseMapper.selectQuestionByIds(questionIds);
        
        if (questions == null || questions.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为VO，并按照题目顺序排序
        List<ExamQuestionVO> result = new ArrayList<>();
        for (ExamAssignmentQuestion relation : questionRelations) {
            // 查找对应的题目
            ExamQuestionBank question = questions.stream()
                    .filter(q -> q.getQuestionId().equals(relation.getQuestionId()))
                    .findFirst()
                    .orElse(null);
            
            if (question != null) {
                ExamQuestionVO vo = new ExamQuestionVO();
                BeanUtils.copyProperties(question, vo);
                
                // 设置分值和顺序
                vo.setPoints(relation.getPoints());
                vo.setQuestionOrder(relation.getQuestionOrder());
                
                // 设置题目类型信息
                if (question.getQuestionTypeId() != null) {
                    ExamQuestionType questionType = questionTypeMapper.selectById(question.getQuestionTypeId());
                    if (questionType != null) {
                        vo.setTypeName(questionType.getTypeName());
                        vo.setIsAutoGraded(questionType.getIsAutoGraded() == 1);
                    }
                }
                
                // 默认设置为未作答
                vo.setIsAnswered(false);
                
                result.add(vo);
            }
        }
        
        return result;
    }
    
    /**
     * 将实体转换为VO对象
     *
     * @param question 题目实体
     * @return 题目VO
     */
    private QuestionVO convertToVO(ExamQuestionBank question) {
        if (question == null) {
            return null;
        }
        
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(question, vo);
        
        // 设置是否自动评分
        if (question.getIsAutoGraded() != null) {
            vo.setIsAutoGraded(question.getIsAutoGraded());
        }
        
        return vo;
    }
} 