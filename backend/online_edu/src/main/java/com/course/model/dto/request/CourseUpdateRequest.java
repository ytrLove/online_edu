package com.course.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程更新请求DTO
 */
@Data
public class CourseUpdateRequest {
    
    /**
     * 课程ID
     * 必填
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    /**
     * 课程名称
     * 长度2-50个字符
     */
    @Size(min = 2, max = 50, message = "课程名称长度必须在2-50个字符之间")
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
     * 课程描述
     */
    private String description;
    
    /**
     * 课程目标
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
     * 教师ID列表
     */
    private List<Long> teacherIds;
    
    /**
     * 备注
     */
    private String remark;
} 