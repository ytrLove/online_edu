package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exam.model.entity.ExamQuestionType;
import com.exam.service.ExamQuestionTypeService;
import com.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目类型控制器
 */
@RestController
@RequestMapping("/api/exam/questionType")
public class ExamQuestionTypeController {
    
    @Autowired
    private ExamQuestionTypeService questionTypeService;
    
    /**
     * 获取题目类型列表
     */
    @PreAuthorize("hasAnyAuthority('exam:questionType:query')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamQuestionType questionType) {
        IPage<ExamQuestionType> page = questionTypeService.selectQuestionTypePage(pageNum, pageSize, questionType);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 获取所有题目类型（不分页）
     */
    @PreAuthorize("hasAnyAuthority('exam:questionType:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        List<ExamQuestionType> list = questionTypeService.list();
        return AjaxResult.success(list);
    }
    
    /**
     * 获取题目类型详细信息
     */
    @PreAuthorize("hasAnyAuthority('exam:questionType:query')")
    @GetMapping(value = "/{typeId}")
    public AjaxResult getInfo(@PathVariable("typeId") Long typeId) {
        return AjaxResult.success(questionTypeService.getById(typeId));
    }
    
    /**
     * 新增题目类型
     */
    @PreAuthorize("hasAnyAuthority('exam:questionType:add')")
    @PostMapping
    public AjaxResult add(@RequestBody ExamQuestionType questionType) {
        return toAjax(questionTypeService.insertQuestionType(questionType));
    }
    
    /**
     * 修改题目类型
     */
    @PreAuthorize("hasAnyAuthority('exam:questionType:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody ExamQuestionType questionType) {
        questionType.setUpdateTime(LocalDateTime.now());
        return toAjax(questionTypeService.updateQuestionType(questionType));
    }
    
    /**
     * 删除题目类型
     */
    @PreAuthorize("hasAnyAuthority('exam:questionType:remove')")
    @DeleteMapping("/{typeIds}")
    public AjaxResult remove(@PathVariable Long[] typeIds) {
        return toAjax(questionTypeService.deleteQuestionTypeByIds(typeIds));
    }
    
    /**
     * 返回操作结果
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.fail();
    }
} 