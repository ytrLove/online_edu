package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.mapper.ExamQuestionTypeMapper;
import com.exam.model.entity.ExamQuestionType;
import com.exam.service.ExamQuestionTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 题目类型服务实现类
 */
@Service
public class ExamQuestionTypeServiceImpl extends ServiceImpl<ExamQuestionTypeMapper, ExamQuestionType> implements ExamQuestionTypeService {

    @Override
    public IPage<ExamQuestionType> selectQuestionTypePage(Integer pageNum, Integer pageSize, ExamQuestionType questionType) {
        Page<ExamQuestionType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ExamQuestionType> queryWrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (questionType != null) {
            // 根据类型名称模糊查询
            if (StringUtils.hasText(questionType.getTypeName())) {
                queryWrapper.like(ExamQuestionType::getTypeName, questionType.getTypeName());
            }
            
            // 根据自动评分筛选
            if (questionType.getIsAutoGraded() != null) {
                queryWrapper.eq(ExamQuestionType::getIsAutoGraded, questionType.getIsAutoGraded());
            }
            
            // 根据状态筛选
            if (questionType.getStatus() != null) {
                queryWrapper.eq(ExamQuestionType::getStatus, questionType.getStatus());
            }
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(ExamQuestionType::getCreateTime);
        
        return page(page, queryWrapper);
    }

    @Override
    public boolean insertQuestionType(ExamQuestionType questionType) {
        return save(questionType);
    }

    @Override
    public boolean updateQuestionType(ExamQuestionType questionType) {
        return updateById(questionType);
    }

    @Override
    public boolean deleteQuestionTypeById(Long typeId) {
        return removeById(typeId);
    }

    @Override
    public boolean deleteQuestionTypeByIds(Long[] typeIds) {
        List<Long> idList = Arrays.asList(typeIds);
        return removeByIds(idList);
    }
} 