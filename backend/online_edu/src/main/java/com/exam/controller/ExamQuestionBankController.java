package com.exam.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.core.domain.AjaxResult;
import com.exam.model.entity.ExamQuestionBank;
import com.exam.model.param.QuestionParam;
import com.exam.model.vo.QuestionVO;
import com.exam.service.ExamQuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 题库控制器
 */
@RestController
@RequestMapping("/api/exam/question")
public class ExamQuestionBankController {
    
    @Autowired
    private ExamQuestionBankService questionBankService;
    
    /**
     * 获取题目列表
     */
    @PreAuthorize("hasAnyAuthority('exam:question:query')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ExamQuestionBank question) {
        IPage<ExamQuestionBank> page = questionBankService.selectQuestionPage(pageNum, pageSize, question);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", page.getRecords());
        data.put("total", page.getTotal());
        return AjaxResult.success(data);
    }
    
    /**
     * 获取题目详细信息
     */
    @PreAuthorize("hasAnyAuthority('exam:question:query')")
    @GetMapping(value = "/{questionId}")
    public AjaxResult getInfo(@PathVariable("questionId") Long questionId) {
        return AjaxResult.success(questionBankService.selectQuestionVOById(questionId));
    }
    
    /**
     * 新增题目
     */
    @PreAuthorize("hasAnyAuthority('exam:question:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody QuestionParam questionParam) {
        return toAjax(questionBankService.insertQuestion(questionParam));
    }
    
    /**
     * 修改题目
     */
    @PreAuthorize("hasAnyAuthority('exam:question:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody QuestionParam questionParam) {
        if (questionParam.getQuestionId() == null) {
            return AjaxResult.fail("题目ID不能为空");
        }
        return toAjax(questionBankService.updateQuestion(questionParam));
    }
    
    /**
     * 删除题目
     */
    @PreAuthorize("hasAnyAuthority('exam:question:remove')")
    @DeleteMapping("/{questionIds}")
    public AjaxResult remove(@PathVariable Long[] questionIds) {
        return toAjax(questionBankService.deleteQuestionByIds(questionIds));
    }
    
    /**
     * 返回操作结果
     */
    private AjaxResult toAjax(boolean result) {
        return result ? AjaxResult.success() : AjaxResult.fail();
    }
} 