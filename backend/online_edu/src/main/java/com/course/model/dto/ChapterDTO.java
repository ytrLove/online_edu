package com.course.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChapterDTO {
    private Long chapterId;
    private Long courseId;
    private String title;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    
    // 课程名称
    private String courseName;
    
    // 小节列表
    private List<SectionDTO> sections;
} 