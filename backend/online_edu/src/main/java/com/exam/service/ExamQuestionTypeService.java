package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.model.entity.ExamQuestionType;

/**
 * 题目类型服务接口
 */
public interface ExamQuestionTypeService extends IService<ExamQuestionType> {
    
    /**
     * 分页查询题目类型
     *
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @param questionType 查询条件
     * @return 分页结果
     */
    IPage<ExamQuestionType> selectQuestionTypePage(Integer pageNum, Integer pageSize, ExamQuestionType questionType);
    
    /**
     * 新增题目类型
     *
     * @param questionType 题目类型信息
     * @return 结果
     */
    boolean insertQuestionType(ExamQuestionType questionType);
    
    /**
     * 修改题目类型
     *
     * @param questionType 题目类型信息
     * @return 结果
     */
    boolean updateQuestionType(ExamQuestionType questionType);
    
    /**
     * 删除题目类型
     *
     * @param typeId 题目类型ID
     * @return 结果
     */
    boolean deleteQuestionTypeById(Long typeId);
    
    /**
     * 批量删除题目类型
     *
     * @param typeIds 需要删除的题目类型ID数组
     * @return 结果
     */
    boolean deleteQuestionTypeByIds(Long[] typeIds);
} 