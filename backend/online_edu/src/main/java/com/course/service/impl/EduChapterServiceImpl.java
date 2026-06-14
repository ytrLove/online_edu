package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.domain.AjaxResult;
import com.course.mapper.EduChapterMapper;
import com.course.mapper.EduSectionMapper;
import com.course.model.dto.ChapterDTO;
import com.course.model.dto.SectionDTO;
import com.course.model.dto.request.ChapterRequest;
import com.course.model.entity.EduChapter;
import com.course.model.entity.EduSection;
import com.course.service.EduChapterService;
import com.course.service.EduSectionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduSectionMapper sectionMapper;
    
    @Autowired
    private EduSectionService sectionService;

    @Override
    public List<ChapterDTO> getChaptersByCourseId(Long courseId) {
        // 查询章节列表
        List<EduChapter> chapters = baseMapper.selectChaptersByCourseId(courseId);
        
        // 转换为DTO列表
        List<ChapterDTO> chapterDTOs = new ArrayList<>();
        for (EduChapter chapter : chapters) {
            ChapterDTO dto = new ChapterDTO();
            BeanUtils.copyProperties(chapter, dto);
            
            // 获取章节下的所有小节
            List<SectionDTO> sectionDTOs = sectionService.getSectionsByChapterId(chapter.getChapterId());
            dto.setSections(sectionDTOs);
            
            chapterDTOs.add(dto);
        }
        
        return chapterDTOs;
    }

    @Override
    public IPage<EduChapter> getChapterPage(Page<EduChapter> page, Long courseId) {
        return baseMapper.selectChapterPage(page, courseId);
    }

    @Override
    public ChapterDTO getChapterById(Long chapterId) {
        // 查询章节
        EduChapter chapter = baseMapper.selectById(chapterId);
        if (chapter == null) {
            return null;
        }
        
        // 转换为DTO
        ChapterDTO dto = new ChapterDTO();
        BeanUtils.copyProperties(chapter, dto);
        
        // 获取章节下的所有小节
        List<SectionDTO> sectionDTOs = sectionService.getSectionsByChapterId(chapterId);
        dto.setSections(sectionDTOs);
        
        return dto;
    }

    @Override
    public AjaxResult addChapter(ChapterRequest request) {
        // 检查章节名称是否已存在
        LambdaQueryWrapper<EduChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduChapter::getCourseId, request.getCourseId())
               .eq(EduChapter::getTitle, request.getTitle())
               .eq(EduChapter::getDelFlag, 0);
        
        if (baseMapper.selectCount(wrapper) > 0) {
            return AjaxResult.fail("章节名称已存在");
        }
        
        // 创建章节实体
        EduChapter chapter = new EduChapter();
        BeanUtils.copyProperties(request, chapter);
        chapter.setDelFlag(0);
        
        // 保存章节
        baseMapper.insert(chapter);
        
        return AjaxResult.success("添加章节成功", chapter);
    }

    @Override
    public AjaxResult updateChapter(ChapterRequest request) {
        // 检查章节是否存在
        EduChapter existingChapter = baseMapper.selectById(request.getChapterId());
        if (existingChapter == null) {
            return AjaxResult.fail("章节不存在");
        }
        
        // 检查章节名称是否已被其他章节使用
        LambdaQueryWrapper<EduChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduChapter::getCourseId, request.getCourseId())
               .eq(EduChapter::getTitle, request.getTitle())
               .eq(EduChapter::getDelFlag, 0)
               .ne(EduChapter::getChapterId, request.getChapterId());
        
        if (baseMapper.selectCount(wrapper) > 0) {
            return AjaxResult.fail("章节名称已被其他章节使用");
        }
        
        // 更新章节
        BeanUtils.copyProperties(request, existingChapter);
        baseMapper.updateById(existingChapter);
        
        return AjaxResult.success("更新章节成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteChapter(Long chapterId) {
        // 检查章节是否存在
        EduChapter chapter = baseMapper.selectById(chapterId);
        if (chapter == null) {
            return AjaxResult.fail("章节不存在");
        }
        
        // 查询章节下是否有小节
        LambdaQueryWrapper<EduSection> sectionWrapper = new LambdaQueryWrapper<>();
        sectionWrapper.eq(EduSection::getChapterId, chapterId)
                     .eq(EduSection::getDelFlag, 0);
        
        if (sectionMapper.selectCount(sectionWrapper) > 0) {
            return AjaxResult.fail("章节下存在小节，请先删除小节");
        }
        
        // 逻辑删除章节
        chapter.setDelFlag(1);
        baseMapper.updateById(chapter);
        
        return AjaxResult.success("删除章节成功");
    }
} 