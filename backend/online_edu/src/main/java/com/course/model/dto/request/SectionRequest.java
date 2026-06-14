package com.course.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SectionRequest {
    private Long sectionId;
    
    @NotNull(message = "章节ID不能为空")
    private Long chapterId;
    
    @NotBlank(message = "小节标题不能为空")
    private String title;
    
    private String content;
    
    private String videoUrl;
    
    private Integer duration;
    
    private String imageUrl;
    
    private String resourceType;
    
    private Boolean freePreview;
    
    private Integer sort;
    
    private Integer status;
    
    private String remark;
} 