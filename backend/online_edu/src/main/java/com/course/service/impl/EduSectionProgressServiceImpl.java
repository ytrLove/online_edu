package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.domain.AjaxResult;
import com.course.mapper.EduSectionMapper;
import com.course.mapper.EduSectionProgressMapper;
import com.course.model.entity.EduSection;
import com.course.model.entity.EduSectionProgress;
import com.course.service.EduSectionProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EduSectionProgressServiceImpl extends ServiceImpl<EduSectionProgressMapper, EduSectionProgress> implements EduSectionProgressService {

    @Autowired
    private EduSectionMapper sectionMapper;

    @Override
    public EduSectionProgress getProgressByStudentAndSection(Long studentId, Long sectionId) {
        return baseMapper.selectByStudentAndSection(studentId, sectionId);
    }

    @Override
    public List<EduSectionProgress> getProgressByCourseAndStudent(Long courseId, Long studentId) {
        return baseMapper.selectByCourseAndStudent(courseId, studentId);
    }

    @Override
    public Integer calculateCourseProgress(Long courseId, Long studentId) {
        Integer progress = baseMapper.calculateCourseProgress(courseId, studentId);
        return progress != null ? progress : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateProgress(Long studentId, Long sectionId, Integer progress, Integer position) {
        // 检查小节是否存在
        EduSection section = sectionMapper.selectById(sectionId);
        if (section == null) {
            return AjaxResult.fail("小节不存在");
        }
        
        // 参数校验
        if (progress < 0 || progress > 100) {
            return AjaxResult.fail("进度取值范围为0-100");
        }
        if (position < 0) {
            return AjaxResult.fail("播放位置不能为负数");
        }
        
        // 查找或创建进度记录
        EduSectionProgress progressRecord = baseMapper.selectByStudentAndSection(studentId, sectionId);
        if (progressRecord == null) {
            progressRecord = new EduSectionProgress();
            progressRecord.setStudentId(studentId);
            progressRecord.setSectionId(sectionId);
            progressRecord.setProgress(progress);
            progressRecord.setLastPosition(position);
            progressRecord.setCompletionStatus(progress >= 100 ? "completed" : "in_progress");
            progressRecord.setCreateTime(LocalDateTime.now());
            progressRecord.setUpdateTime(LocalDateTime.now());
            baseMapper.insert(progressRecord);
        } else {
            // 只有新进度大于旧进度时才更新
            if (progress > progressRecord.getProgress()) {
                progressRecord.setProgress(progress);
            }
            progressRecord.setLastPosition(position);
            progressRecord.setCompletionStatus(progress >= 100 ? "completed" : "in_progress");
            progressRecord.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(progressRecord);
        }
        
        return AjaxResult.success("更新学习进度成功", progressRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult completeSection(Long studentId, Long sectionId) {
        // 检查小节是否存在
        EduSection section = sectionMapper.selectById(sectionId);
        if (section == null) {
            return AjaxResult.fail("小节不存在");
        }
        
        // 查找或创建进度记录
        EduSectionProgress progressRecord = baseMapper.selectByStudentAndSection(studentId, sectionId);
        if (progressRecord == null) {
            progressRecord = new EduSectionProgress();
            progressRecord.setStudentId(studentId);
            progressRecord.setSectionId(sectionId);
            progressRecord.setProgress(100);
            progressRecord.setLastPosition(0);
            progressRecord.setCompletionStatus("completed");
            progressRecord.setCreateTime(LocalDateTime.now());
            progressRecord.setUpdateTime(LocalDateTime.now());
            baseMapper.insert(progressRecord);
        } else {
            progressRecord.setProgress(100);
            progressRecord.setCompletionStatus("completed");
            progressRecord.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(progressRecord);
        }
        
        return AjaxResult.success("已完成学习", progressRecord);
    }
} 