package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.common.core.domain.AjaxResult;
import com.course.model.dto.SectionDTO;
import com.course.model.dto.request.SectionRequest;
import com.course.model.entity.EduSection;
import java.util.List;

/**
 * 课程小节服务接口
 */
public interface EduSectionService extends IService<EduSection> {
    
    /**
     * 根据章节ID获取所有小节
     * 
     * @param chapterId 章节ID
     * @return 小节列表
     */
    List<SectionDTO> getSectionsByChapterId(Long chapterId);
    
    /**
     * 根据课程ID获取所有小节
     * 
     * @param courseId 课程ID
     * @return 小节列表
     */
    List<SectionDTO> getSectionsByCourseId(Long courseId);
    
    /**
     * 分页查询章节小节
     * 
     * @param page 分页参数
     * @param chapterId 章节ID
     * @return 小节分页列表
     */
    IPage<EduSection> getSectionPage(Page<EduSection> page, Long chapterId);
    
    /**
     * 获取小节详情
     * 
     * @param sectionId 小节ID
     * @return 小节详情
     */
    SectionDTO getSectionById(Long sectionId);
    
    /**
     * 新增小节
     * 
     * @param request 小节信息
     * @return 添加结果
     */
    AjaxResult addSection(SectionRequest request);
    
    /**
     * 更新小节
     * 
     * @param request 小节信息
     * @return 更新结果
     */
    AjaxResult updateSection(SectionRequest request);
    
    /**
     * 删除小节
     * 
     * @param sectionId 小节ID
     * @return 删除结果
     */
    AjaxResult deleteSection(Long sectionId);
} 