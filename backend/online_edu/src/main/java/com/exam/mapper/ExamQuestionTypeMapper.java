package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.model.entity.ExamQuestionType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目类型数据访问层
 */
@Mapper
public interface ExamQuestionTypeMapper extends BaseMapper<ExamQuestionType> {
    
} 