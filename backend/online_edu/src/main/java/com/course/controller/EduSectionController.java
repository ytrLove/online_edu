package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.core.domain.AjaxResult;
import com.common.core.page.TableDataInfo;
import com.course.model.dto.SectionDTO;
import com.course.model.dto.request.SectionRequest;
import com.course.model.entity.EduSection;
import com.course.service.EduSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程小节控制器
 */
@RestController
@RequestMapping("/course/section")
public class EduSectionController {

    @Autowired
    private EduSectionService sectionService;

    /**
     * 根据章节ID获取小节列表
     */
    @GetMapping("/list/chapter/{chapterId}")
    public AjaxResult getSectionsByChapter(@PathVariable Long chapterId) {
        List<SectionDTO> sections = sectionService.getSectionsByChapterId(chapterId);
        return AjaxResult.success("获取小节列表成功", sections);
    }

    /**
     * 根据课程ID获取所有小节
     */
    @GetMapping("/list/course/{courseId}")
    public AjaxResult getSectionsByCourse(@PathVariable Long courseId) {
        List<SectionDTO> sections = sectionService.getSectionsByCourseId(courseId);
        return AjaxResult.success("获取小节列表成功", sections);
    }

    /**
     * 分页查询小节
     */
    @GetMapping("/page/{chapterId}")
    public TableDataInfo getSectionPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @PathVariable Long chapterId) {
        Page<EduSection> page = new Page<>(pageNum, pageSize);
        IPage<EduSection> pageResult = sectionService.getSectionPage(page, chapterId);
        
        TableDataInfo tableData = new TableDataInfo();
        tableData.setTotal(pageResult.getTotal());
        tableData.setRows(pageResult.getRecords());
        return tableData;
    }

    /**
     * 获取小节详情
     */
    @GetMapping("/{sectionId}")
    public AjaxResult getSectionDetail(@PathVariable Long sectionId) {
        SectionDTO section = sectionService.getSectionById(sectionId);
        if (section == null) {
            return AjaxResult.fail("小节不存在");
        }
        return AjaxResult.success("获取小节详情成功", section);
    }

    /**
     * 添加小节
     */
    @PostMapping
    public AjaxResult addSection(@RequestBody @Validated SectionRequest request) {
        return sectionService.addSection(request);
    }

    /**
     * 更新小节
     */
    @PutMapping
    public AjaxResult updateSection(@RequestBody @Validated SectionRequest request) {
        if (request.getSectionId() == null) {
            return AjaxResult.fail("小节ID不能为空");
        }
        return sectionService.updateSection(request);
    }

    /**
     * 删除小节
     */
    @DeleteMapping("/{sectionId}")
    public AjaxResult deleteSection(@PathVariable Long sectionId) {
        return sectionService.deleteSection(sectionId);
    }
} 