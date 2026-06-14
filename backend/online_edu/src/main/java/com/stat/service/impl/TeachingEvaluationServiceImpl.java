package com.stat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.mapper.EduCourseTeacherMapper;
import com.course.model.entity.EduCourseTeacher;
import com.course.model.entity.EduTeacher;
import com.stat.mapper.TeachingEvaluationMapper;
import com.stat.model.entity.TeachingEvaluation;
import com.stat.service.TeachingEvaluationService;
import com.util.GetId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 教学评价服务实现类
 */
@Service
@RequiredArgsConstructor
public class TeachingEvaluationServiceImpl extends ServiceImpl<TeachingEvaluationMapper, TeachingEvaluation> implements TeachingEvaluationService {

    private final TeachingEvaluationMapper teachingEvaluationMapper;
    private final EduCourseTeacherMapper courseTeacherMapper;
    @Autowired
    private GetId getId;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitEvaluation(TeachingEvaluation evaluation) {
        // 如果没有设置教师ID，则自动获取
        if (evaluation.getTeacherId() == null) {
            Long teacherId = getTeacherIdByCourseId(evaluation.getCourseId());
            evaluation.setTeacherId(teacherId);

            // 获取学生id
            Long studentId = getId.getId();
            evaluation.setStudentId(studentId);
        }
        
        evaluation.setEvaluateTime(LocalDateTime.now());
        return save(evaluation);
    }

    @Override
    public Long getTeacherIdByCourseId(Long courseId) {
        // 查询课程的主讲教师
        EduCourseTeacher courseTeacher = courseTeacherMapper.selectOne(
            new LambdaQueryWrapper<EduCourseTeacher>()
                .eq(EduCourseTeacher::getCourseId, courseId)
                .eq(EduCourseTeacher::getRole, "main")
                .last("LIMIT 1")
        );
        
        if (courseTeacher != null) {
            return courseTeacher.getTeacherId();
        }
        
        // 如果没有主讲教师，则获取第一个教师
        courseTeacher = courseTeacherMapper.selectOne(
            new LambdaQueryWrapper<EduCourseTeacher>()
                .eq(EduCourseTeacher::getCourseId, courseId)
                .last("LIMIT 1")
        );
        
        return courseTeacher != null ? courseTeacher.getTeacherId() : null;
    }

    @Override
    public IPage<TeachingEvaluation> getTeacherEvaluationPage(
            Page<TeachingEvaluation> page,
            Long teacherId,
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return teachingEvaluationMapper.selectTeacherEvaluationPage(page, teacherId, courseId, startTime, endTime);
    }

    @Override
    public Map<String, Object> getCourseEvaluationStats(Long courseId) {
        return teachingEvaluationMapper.selectCourseEvaluationStats(courseId);
    }

    @Override
    public Map<String, Object> getCourseScoreTrend(
            Long courseId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return teachingEvaluationMapper.selectCourseScoreTrend(courseId, startTime, endTime);
    }

    @Override
    public Map<String, Object> getTeacherScoreStats(Long courseId) {
        return teachingEvaluationMapper.selectTeacherScoreStats(courseId);
    }

    @Override
    public IPage<TeachingEvaluation> getEvaluationPage(
            Page<TeachingEvaluation> page,
            String teacherName,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return teachingEvaluationMapper.selectEvaluationPage(page, teacherName, startTime, endTime);
    }
}