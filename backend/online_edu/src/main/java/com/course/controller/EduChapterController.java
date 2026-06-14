package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.common.core.page.TableDataInfo;
import com.course.model.dto.ChapterDTO;
import com.course.model.dto.request.ChapterRequest;
import com.course.model.entity.EduChapter;
import com.course.service.EduChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程章节控制器
 */
@RestController
@RequestMapping("/course/chapter")
public class EduChapterController {

    @Autowired
    private EduChapterService chapterService;

    /**
     * 获取课程的所有章节
     */
    @GetMapping("/list/{courseId}")
    public AjaxResult getChapterList(@PathVariable Long courseId) {
        List<ChapterDTO> chapters = chapterService.getChaptersByCourseId(courseId);
        return AjaxResult.success("获取章节列表成功", chapters);
    }

    /**
     * 分页查询课程章节
     */
    @GetMapping("/page/{courseId}")
    public TableDataInfo getChapterPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @PathVariable Long courseId) {
        Page<EduChapter> page = new Page<>(pageNum, pageSize);
        IPage<EduChapter> pageResult = chapterService.getChapterPage(page, courseId);
        
        TableDataInfo tableData = new TableDataInfo();
        tableData.setTotal(pageResult.getTotal());
        tableData.setRows(pageResult.getRecords());
        return tableData;
    }

    /**
     * 获取章节详情
     */
    @GetMapping("/{chapterId}")
    public AjaxResult getChapterDetail(@PathVariable Long chapterId) {
        ChapterDTO chapter = chapterService.getChapterById(chapterId);
        if (chapter == null) {
            return AjaxResult.fail("章节不存在");
        }
        return AjaxResult.success("获取章节详情成功", chapter);
    }

    /**
     * 添加章节
     */
    @PostMapping
    public AjaxResult addChapter(@RequestBody @Validated ChapterRequest request) {
        return chapterService.addChapter(request);
    }

    /**
     * 更新章节
     */
    @PutMapping
    public AjaxResult updateChapter(@RequestBody @Validated ChapterRequest request) {
        if (request.getChapterId() == null) {
            return AjaxResult.fail("章节ID不能为空");
        }
        return chapterService.updateChapter(request);
    }

    /**
     * 删除章节
     */
    @DeleteMapping("/{chapterId}")
    public AjaxResult deleteChapter(@PathVariable Long chapterId) {
        return chapterService.deleteChapter(chapterId);
    }
} 