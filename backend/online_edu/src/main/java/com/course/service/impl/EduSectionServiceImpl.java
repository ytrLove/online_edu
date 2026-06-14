package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.domain.AjaxResult;
import com.course.mapper.EduChapterMapper;
import com.course.mapper.EduSectionMapper;
import com.course.model.dto.SectionDTO;
import com.course.model.dto.request.SectionRequest;
import com.course.model.entity.EduChapter;
import com.course.model.entity.EduSection;
import com.course.service.EduSectionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EduSectionServiceImpl extends ServiceImpl<EduSectionMapper, EduSection> implements EduSectionService {

    @Autowired
    private EduChapterMapper chapterMapper;

    @Override
    public List<SectionDTO> getSectionsByChapterId(Long chapterId) {
        // 查询小节列表
        List<EduSection> sections = baseMapper.selectSectionsByChapterId(chapterId);
        
        // 转换为DTO列表
        List<SectionDTO> sectionDTOs = new ArrayList<>();
        for (EduSection section : sections) {
            SectionDTO dto = new SectionDTO();
            BeanUtils.copyProperties(section, dto);
            sectionDTOs.add(dto);
        }
        
        return sectionDTOs;
    }

    @Override
    public List<SectionDTO> getSectionsByCourseId(Long courseId) {
        // 查询小节列表
        List<EduSection> sections = baseMapper.selectSectionsByCourseId(courseId);
        
        // 转换为DTO列表
        List<SectionDTO> sectionDTOs = new ArrayList<>();
        for (EduSection section : sections) {
            SectionDTO dto = new SectionDTO();
            BeanUtils.copyProperties(section, dto);
            sectionDTOs.add(dto);
        }
        
        return sectionDTOs;
    }

    @Override
    public IPage<EduSection> getSectionPage(Page<EduSection> page, Long chapterId) {
        return baseMapper.selectSectionPage(page, chapterId);
    }

    @Override
    public SectionDTO getSectionById(Long sectionId) {
        // 查询小节
        EduSection section = baseMapper.selectById(sectionId);
        if (section == null) {
            return null;
        }
        
        // 转换为DTO
        SectionDTO dto = new SectionDTO();
        BeanUtils.copyProperties(section, dto);
        
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addSection(SectionRequest request) {
        // 检查章节是否存在
        EduChapter chapter = chapterMapper.selectById(request.getChapterId());
        if (chapter == null) {
            return AjaxResult.fail("所属章节不存在");
        }
        
        // 检查小节名称是否已存在
        LambdaQueryWrapper<EduSection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduSection::getChapterId, request.getChapterId())
               .eq(EduSection::getTitle, request.getTitle())
               .eq(EduSection::getDelFlag, 0);
        
        if (baseMapper.selectCount(wrapper) > 0) {
            return AjaxResult.fail("小节名称已存在");
        }
        
        // 创建小节实体
        EduSection section = new EduSection();
        BeanUtils.copyProperties(request, section);
        section.setDelFlag(0);
        
        // 设置默认值
        if (section.getResourceType() == null) {
            section.setResourceType("video");
        }
        if (section.getFreePreview() == null) {
            section.setFreePreview(false);
        }
        if (section.getSort() == null) {
            section.setSort(0);
        }
        if (section.getStatus() == null) {
            section.setStatus(1);
        }
        
        // 保存小节
        baseMapper.insert(section);
        
        return AjaxResult.success("添加小节成功", section);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateSection(SectionRequest request) {
        // 检查小节是否存在
        EduSection existingSection = baseMapper.selectById(request.getSectionId());
        if (existingSection == null) {
            return AjaxResult.fail("小节不存在");
        }
        
        // 检查章节是否存在
        EduChapter chapter = chapterMapper.selectById(request.getChapterId());
        if (chapter == null) {
            return AjaxResult.fail("所属章节不存在");
        }
        
        // 检查小节名称是否已被其他小节使用
        LambdaQueryWrapper<EduSection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduSection::getChapterId, request.getChapterId())
               .eq(EduSection::getTitle, request.getTitle())
               .eq(EduSection::getDelFlag, 0)
               .ne(EduSection::getSectionId, request.getSectionId());
        
        if (baseMapper.selectCount(wrapper) > 0) {
            return AjaxResult.fail("小节名称已被其他小节使用");
        }
        
        // 更新小节
        BeanUtils.copyProperties(request, existingSection);
        baseMapper.updateById(existingSection);
        
        return AjaxResult.success("更新小节成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteSection(Long sectionId) {
        // 检查小节是否存在
        EduSection section = baseMapper.selectById(sectionId);
        if (section == null) {
            return AjaxResult.fail("小节不存在");
        }
        
        // 逻辑删除小节
        section.setDelFlag(1);
        baseMapper.updateById(section);
        
        return AjaxResult.success("删除小节成功");
    }
} 