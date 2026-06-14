-- ----------------------------
-- 课程章节与小节数据库结构
-- ----------------------------

-- 课程章节表
DROP TABLE IF EXISTS `edu_chapter`;
CREATE TABLE `edu_chapter` (
  `chapter_id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) NOT NULL COMMENT '章节标题',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0隐藏）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`chapter_id`),
  INDEX `idx_course_id` (`course_id`),
  CONSTRAINT `fk_chapter_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='课程章节表';

-- 课程小节表
DROP TABLE IF EXISTS `edu_section`;
CREATE TABLE `edu_section` (
  `section_id` bigint NOT NULL AUTO_INCREMENT COMMENT '小节ID',
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `title` varchar(100) NOT NULL COMMENT '小节标题',
  `content` text COMMENT '小节内容',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `duration` int DEFAULT 0 COMMENT '视频时长（秒）',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `resource_type` varchar(20) DEFAULT 'video' COMMENT '资源类型（video视频 text文本 image图片 audio音频 attachment附件）',
  `free_preview` tinyint(1) DEFAULT 0 COMMENT '是否免费预览（0否 1是）',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0隐藏）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`section_id`),
  INDEX `idx_chapter_id` (`chapter_id`),
  CONSTRAINT `fk_section_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `edu_chapter` (`chapter_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='课程小节表';

-- 课程学习进度记录表
DROP TABLE IF EXISTS `edu_section_progress`;
CREATE TABLE `edu_section_progress` (
  `progress_id` bigint NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `section_id` bigint NOT NULL COMMENT '小节ID',
  `progress` int DEFAULT 0 COMMENT '学习进度（0-100）',
  `completion_status` varchar(20) DEFAULT 'not_started' COMMENT '完成状态（not_started未开始 in_progress进行中 completed已完成）',
  `last_position` int DEFAULT 0 COMMENT '上次观看位置（秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`progress_id`),
  UNIQUE KEY `idx_student_section` (`student_id`, `section_id`),
  INDEX `idx_section_id` (`section_id`),
  CONSTRAINT `fk_progress_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_progress_section` FOREIGN KEY (`section_id`) REFERENCES `edu_section` (`section_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='课程学习进度记录表'; 