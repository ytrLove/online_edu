package com.course.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SectionDTO {
    private Long sectionId;
    private Long chapterId;
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private String imageUrl;
    private String resourceType;
    private Boolean freePreview;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    
    // 章节标题
    private String chapterTitle;
} 