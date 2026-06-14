package com.home.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.mapper.EduCourseMapper;
import com.course.mapper.EduStudentMapper;
import com.course.mapper.EduTeachingResourceMapper;
import com.course.model.dto.response.CourseListResponse;
import com.course.model.entity.EduCourse;
import com.course.model.entity.EduStudent;
import com.course.model.entity.EduTeachingResource;
import com.exam.model.entity.ExamAssignment;
import com.exam.model.vo.AssignmentVO;
import com.exam.service.ExamAssignmentService;
import com.home.domain.DashboardResponse;
import com.home.service.DashboardService;
import com.util.GetId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private EduStudentMapper eduStudentMapper;
    
    @Autowired
    private EduCourseMapper eduCourseMapper;
    
    @Autowired
    private EduTeachingResourceMapper resourceMapper;
    
    @Autowired
    private ExamAssignmentService assignmentService;
    
    @Autowired
    private GetId getId;

    @Override
    public DashboardResponse getInfo() {
        // 获取student_id
        Long studentId = getId.getId();
        // 获取学生详细
        EduStudent student = eduStudentMapper.selectStudentWithUserInfoById(studentId);
        if (student == null) {
            return null;
        }
        
        // 获取课程列表
        IPage<CourseListResponse> myCourses = eduCourseMapper.selectCourseList(new Page<>(1, 5), new EduCourse());

        DashboardResponse response = new DashboardResponse();
        response.setCourseCount((int) myCourses.getTotal());
        response.setMyCourses(myCourses);
        
        // 获取所有资源
        int resourceCount = 0;
        for(CourseListResponse course: myCourses.getRecords()) {
            // 获取课程id
            Long courseId = course.getCourseId();
            // 通过课程id获取当前课程的资源数
            List<EduTeachingResource> resources = resourceMapper.selectResourcesByCourseId(courseId);
            resourceCount += (resources != null ? resources.size() : 0);
        }
        response.setResourceCount(resourceCount);
        
        // 获取待完成作业列表
        ExamAssignment condition = new ExamAssignment();
        condition.setStatus(1); // 有效状态
        IPage<ExamAssignment> assignmentPage = assignmentService.selectStudentAssignmentPage(1, 5, studentId, condition);
        
        // 转换为VO列表
        List<AssignmentVO> pendingAssignments = new ArrayList<>();
        if (assignmentPage.getRecords() != null && !assignmentPage.getRecords().isEmpty()) {
            for (ExamAssignment assignment : assignmentPage.getRecords()) {
                AssignmentVO vo = assignmentService.selectStudentAssignmentVOById(assignment.getAssignmentId(), studentId);
                if (vo != null && vo.getSubmission() == null) {
                    pendingAssignments.add(vo);
                }
            }
        }
        
        response.setPendingAssignments(pendingAssignments);
        response.setAssignmentCount(pendingAssignments.size());

        return response;
    }
    
    @Override
    public List<Map<String, Object>> getRecentActivities() {
        Long studentId = getId.getId();
        
        // 模拟获取最近活动数据
        // 实际项目中应该从数据库中查询
        List<Map<String, Object>> activities = new ArrayList<>();
        
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("id", 1L);
        activity1.put("type", "课程学习");
        activity1.put("content", "完成了《Java编程基础》第3章节");
        activity1.put("createTime", LocalDateTime.now().minusDays(1));
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("id", 2L);
        activity2.put("type", "作业提交");
        activity2.put("content", "提交了《数据结构》第二次作业");
        activity2.put("createTime", LocalDateTime.now().minusDays(2));
        
        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("id", 3L);
        activity3.put("type", "考试");
        activity3.put("content", "参加了《软件工程》期中考试");
        activity3.put("createTime", LocalDateTime.now().minusDays(5));
        
        activities.add(activity1);
        activities.add(activity2);
        activities.add(activity3);
        
        return activities;
    }
    
    @Override
    public Map<String, Object> getLearningProgress() {
        Long studentId = getId.getId();
        
        // 模拟获取学习进度数据
        // 实际项目中应该从数据库中查询
        Map<String, Object> progress = new HashMap<>();
        
        // 总体学习进度
        progress.put("totalProgress", 75);
        
        // 各课程学习进度
        List<Map<String, Object>> courseProgressList = new ArrayList<>();
        
        Map<String, Object> courseProgress1 = new HashMap<>();
        courseProgress1.put("courseId", 1L);
        courseProgress1.put("courseName", "Java编程基础");
        courseProgress1.put("progress", 90);
        
        Map<String, Object> courseProgress2 = new HashMap<>();
        courseProgress2.put("courseId", 2L);
        courseProgress2.put("courseName", "数据结构");
        courseProgress2.put("progress", 65);
        
        Map<String, Object> courseProgress3 = new HashMap<>();
        courseProgress3.put("courseId", 3L);
        courseProgress3.put("courseName", "软件工程");
        courseProgress3.put("progress", 70);
        
        courseProgressList.add(courseProgress1);
        courseProgressList.add(courseProgress2);
        courseProgressList.add(courseProgress3);
        
        progress.put("courseProgress", courseProgressList);
        
        return progress;
    }
    
    @Override
    public List<Map<String, Object>> getNotifications() {
        Long studentId = getId.getId();
        
        // 模拟获取通知数据
        // 实际项目中应该从数据库中查询
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        Map<String, Object> notification1 = new HashMap<>();
        notification1.put("id", 1L);
        notification1.put("title", "新作业发布");
        notification1.put("content", "《数据结构》课程发布了新的作业，请及时完成");
        notification1.put("createTime", LocalDateTime.now().minusHours(5));
        notification1.put("isRead", false);
        
        Map<String, Object> notification2 = new HashMap<>();
        notification2.put("id", 2L);
        notification2.put("title", "课程提醒");
        notification2.put("content", "《Java编程基础》将于明天下午2点进行线上直播课");
        notification2.put("createTime", LocalDateTime.now().minusDays(1));
        notification2.put("isRead", true);
        
        Map<String, Object> notification3 = new HashMap<>();
        notification3.put("id", 3L);
        notification3.put("title", "系统通知");
        notification3.put("content", "系统将于本周日凌晨2点-4点进行维护，请提前做好准备");
        notification3.put("createTime", LocalDateTime.now().minusDays(2));
        notification3.put("isRead", true);
        
        notifications.add(notification1);
        notifications.add(notification2);
        notifications.add(notification3);
        
        return notifications;
    }
    
    @Override
    public Map<String, Object> getCourseDashboard(Long courseId) {
        Long studentId = getId.getId();
        
        // 模拟获取课程仪表盘数据
        // 实际项目中应该从数据库中查询
        Map<String, Object> courseDashboard = new HashMap<>();
        
        // 课程基本信息
        EduCourse course = eduCourseMapper.selectById(courseId);
        if (course != null) {
            courseDashboard.put("courseId", course.getCourseId());
            courseDashboard.put("courseName", course.getCourseName());
            courseDashboard.put("courseDescription", course.getDescription());
            
            // 课程学习进度
            courseDashboard.put("progress", 75);
            
            // 课程资源统计
            List<EduTeachingResource> resources = resourceMapper.selectResourcesByCourseId(courseId);
            courseDashboard.put("resourceCount", resources != null ? resources.size() : 0);
            
            // 待完成作业统计
            ExamAssignment condition = new ExamAssignment();
            condition.setCourseId(courseId);
            condition.setStatus(1); // 有效状态
            IPage<ExamAssignment> assignmentPage = assignmentService.selectStudentAssignmentPage(1, 100, studentId, condition);
            
            int pendingCount = 0;
            if (assignmentPage.getRecords() != null) {
                for (ExamAssignment assignment : assignmentPage.getRecords()) {
                    AssignmentVO vo = assignmentService.selectStudentAssignmentVOById(assignment.getAssignmentId(), studentId);
                    if (vo != null && vo.getSubmission() == null) {
                        pendingCount++;
                    }
                }
            }
            courseDashboard.put("pendingAssignmentCount", pendingCount);
            
            // 最近活动
            List<Map<String, Object>> recentActivities = getRecentActivities().stream()
                    .filter(activity -> activity.get("content").toString().contains(course.getCourseName()))
                    .collect(Collectors.toList());
            courseDashboard.put("recentActivities", recentActivities);
        }
        
        return courseDashboard;
    }
}
