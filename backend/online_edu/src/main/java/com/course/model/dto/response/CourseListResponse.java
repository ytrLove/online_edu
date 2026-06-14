package com.course.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程列表项响应DTO
 */
@Data
public class CourseListResponse {
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 课程代码
     */
    private String courseCode;
    
    /**
     * 科目ID
     */
    private Long subjectId;
    
    /**
     * 科目名称
     */
    private String subjectName;
    
    /**
     * 课程描述
     */
    private String description;

    /**
     * 目标
     */
    private String objectives;

    /**
     * 课程大纲
     */
    private String syllabus;
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 课程状态 (draft, published, closed)
     */
    private String status;
    
    /**
     * 选课开始时间
     */
    private LocalDateTime enrollmentStartTime;
    
    /**
     * 选课结束时间
     */
    private LocalDateTime enrollmentEndTime;
    
    /**
     * 选课状态 (open, closed, upcoming)
     */
    private String enrollmentStatus;
    
    /**
     * 学生数量
     */
    private Integer studentCount;
    
    /**
     * 主讲教师名称
     */
    private String mainTeacherName;
    
    /**
     * 教师ID列表
     */
    private List<Long> teacherIds;
    
    /**
     * 教师名称
     */
    private String teacherName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;
} 