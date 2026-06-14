package com.course.controller;

import com.common.core.domain.AjaxResult;
import com.course.model.entity.EduSubject;
import com.course.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学科管理控制器
 */
@RestController
@RequestMapping("/api/edu/subject")
public class EduSubjectController {

    @Autowired
    private EduSubjectService subjectService;

    /**
     * 获取所有学科(树形结构)
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('edu:subject:query')")
    public AjaxResult<List<EduSubject>> tree() {
        List<EduSubject> tree = subjectService.getSubjectTree();
        return AjaxResult.success(tree);
    }

    /**
     * 获取所有顶级学科
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('edu:subject:query')")
    public AjaxResult<List<EduSubject>> list() {
        List<EduSubject> list = subjectService.getTopSubjects();
        return AjaxResult.success(list);
    }

    /**
     * 根据父ID获取子学科
     */
    @GetMapping("/children/{parentId}")
    @PreAuthorize("hasAuthority('edu:subject:query')")
    public AjaxResult<List<EduSubject>> children(@PathVariable Long parentId) {
        List<EduSubject> children = subjectService.getChildrenByParentId(parentId);
        return AjaxResult.success(children);
    }

    /**
     * 获取学科详情
     */
    @GetMapping("/{subjectId}")
    @PreAuthorize("hasAuthority('edu:subject:query')")
    public AjaxResult<EduSubject> getInfo(@PathVariable Long subjectId) {
        EduSubject subject = subjectService.getById(subjectId);
        return AjaxResult.success(subject);
    }

    /**
     * 新增学科
     */
    @PostMapping
    @PreAuthorize("hasAuthority('edu:subject:add')")
    public AjaxResult<EduSubject> add(@RequestBody EduSubject subject) {
        subjectService.save(subject);
        return AjaxResult.success(subject);
    }

    /**
     * 修改学科
     */
    @PutMapping
    @PreAuthorize("hasAuthority('edu:subject:edit')")
    public AjaxResult<EduSubject> update(@RequestBody EduSubject subject) {
        subjectService.updateById(subject);
        return AjaxResult.success(subject);
    }

    /**
     * 删除学科
     */
    @DeleteMapping("/{subjectId}")
    @PreAuthorize("hasAuthority('edu:subject:remove')")
    public AjaxResult<Boolean> remove(@PathVariable Long subjectId) {
        boolean result = subjectService.removeSubject(subjectId);
        return AjaxResult.success(result);
    }
} 