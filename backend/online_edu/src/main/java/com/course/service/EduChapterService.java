package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.core.domain.AjaxResult;
import com.course.model.dto.ChapterDTO;
import com.course.model.dto.request.ChapterRequest;
import com.course.model.entity.EduChapter;
import java.util.List;

/**
 * 课程章节服务接口
 */
public interface EduChapterService extends IService<EduChapter> {
    
    /**
     * 获取课程的所有章节及小节
     * 
     * @param courseId 课程ID
     * @return 章节列表（包含小节）
     */
    List<ChapterDTO> getChaptersByCourseId(Long courseId);
    
    /**
     * 分页查询课程章节
     * 
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 章节分页列表
     */
    IPage<EduChapter> getChapterPage(Page<EduChapter> page, Long courseId);
    
    /**
     * 获取章节详情
     * 
     * @param chapterId 章节ID
     * @return 章节详情
     */
    ChapterDTO getChapterById(Long chapterId);
    
    /**
     * 新增章节
     * 
     * @param request 章节信息
     * @return 添加结果
     */
    AjaxResult addChapter(ChapterRequest request);
    
    /**
     * 更新章节
     * 
     * @param request 章节信息
     * @return 更新结果
     */
    AjaxResult updateChapter(ChapterRequest request);
    
    /**
     * 删除章节
     * 
     * @param chapterId 章节ID
     * @return 删除结果
     */
    AjaxResult deleteChapter(Long chapterId);
} 