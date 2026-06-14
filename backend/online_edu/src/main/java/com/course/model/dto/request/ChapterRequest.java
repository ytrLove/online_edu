package com.course.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ChapterRequest {
    private Long chapterId;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotBlank(message = "章节标题不能为空")
    private String title;
    
    private Integer sort;
    
    private Integer status;
    
    private String remark;
} 