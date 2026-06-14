package com.course.model.dto.request;

import lombok.Data;

/**
 * 课程查询请求DTO
 */
@Data
public class CourseQueryRequest {
    
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
     * 课程状态 (draft, published, closed)
     */
    private String status;
    
    /**
     * 教师ID（查询该教师的课程）
     */
    private Long teacherId;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
} 