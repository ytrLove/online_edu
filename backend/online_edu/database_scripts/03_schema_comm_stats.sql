-- ----------------------------
-- 5. 互动交流模块
-- ----------------------------

-- 讨论区表
DROP TABLE IF EXISTS `comm_discussion_forum`;
CREATE TABLE `comm_discussion_forum` (
  `forum_id` bigint NOT NULL AUTO_INCREMENT COMMENT '论坛ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `forum_name` varchar(100) NOT NULL COMMENT '论坛名称',
  `description` text COMMENT '描述',
  `is_public` int DEFAULT 0 COMMENT '是否公开（0否 1是）',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`forum_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `fk_forum_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='讨论区表';

-- 讨论主题表
DROP TABLE IF EXISTS `comm_discussion_topic`;
CREATE TABLE `comm_discussion_topic` (
  `topic_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `forum_id` bigint NOT NULL COMMENT '论坛ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '创建者ID',
  `last_reply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后回复时间',
  `view_count` int DEFAULT 0 COMMENT '浏览数',
  `reply_count` int DEFAULT 0 COMMENT '回复数',
  `is_pinned` int DEFAULT 0 COMMENT '是否置顶（0否 1是）',
  `status` varchar(20) DEFAULT 'normal' COMMENT '状态（normal正常 hidden隐藏）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`topic_id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_topic_forum` FOREIGN KEY (`forum_id`) REFERENCES `comm_discussion_forum` (`forum_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_topic_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='讨论主题表';

-- 讨论回复表
DROP TABLE IF EXISTS `comm_discussion_reply`;
CREATE TABLE `comm_discussion_reply` (
  `reply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `topic_id` bigint NOT NULL COMMENT '主题ID',
  `content` text NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '创建者ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父回复ID',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`reply_id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `fk_reply_topic` FOREIGN KEY (`topic_id`) REFERENCES `comm_discussion_topic` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_reply_parent` FOREIGN KEY (`parent_id`) REFERENCES `comm_discussion_reply` (`reply_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='讨论回复表';

-- 私信表
DROP TABLE IF EXISTS `comm_private_message`;
CREATE TABLE `comm_private_message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '内容',
  `is_read` int DEFAULT 0 COMMENT '是否已读（0否 1是）',
  `del_flag_sender` int DEFAULT 0 COMMENT '发送者删除标志（0代表存在 1代表删除）',
  `del_flag_receiver` int DEFAULT 0 COMMENT '接收者删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`message_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='私信表';

-- ----------------------------
-- 6. 学习分析与评价模块
-- ----------------------------

-- 学习行为记录表
DROP TABLE IF EXISTS `stat_learning_behavior`;
CREATE TABLE `stat_learning_behavior` (
  `behavior_id` bigint NOT NULL AUTO_INCREMENT COMMENT '行为ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `behavior_type` varchar(50) NOT NULL COMMENT '行为类型(view_resource查看资源 submit_assignment提交作业 take_exam参加考试 post_discussion发表讨论)',
  `resource_id` bigint DEFAULT NULL COMMENT '资源ID',
  `duration` int DEFAULT NULL COMMENT '持续时间(秒)',
  `behavior_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  `client_info` varchar(255) DEFAULT NULL COMMENT '客户端信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`behavior_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_behavior_time` (`behavior_time`),
  CONSTRAINT `fk_behavior_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_behavior_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='学习行为记录表';

-- 学习概要表
DROP TABLE IF EXISTS `stat_learning_summary`;
CREATE TABLE `stat_learning_summary` (
  `summary_id` bigint NOT NULL AUTO_INCREMENT COMMENT '概要ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `total_study_time` int DEFAULT 0 COMMENT '总学习时长(分钟)',
  `resource_access_count` int DEFAULT 0 COMMENT '资源访问次数',
  `assignment_completion` int DEFAULT 0 COMMENT '作业完成率(%)',
  `exam_avg_score` decimal(5,2) DEFAULT 0.00 COMMENT '考试平均分',
  `discussion_count` int DEFAULT 0 COMMENT '讨论参与次数',
  `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
  `knowledge_mastery` varchar(2000) DEFAULT NULL COMMENT '知识掌握情况(JSON)',
  `interest_points` varchar(2000) DEFAULT NULL COMMENT '兴趣点数据(JSON)',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`summary_id`),
  UNIQUE KEY `idx_student_course` (`student_id`, `course_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `fk_summary_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_summary_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='学习概要表';

-- 教学评价表
DROP TABLE IF EXISTS `stat_teaching_evaluation`;
CREATE TABLE `stat_teaching_evaluation` (
  `evaluation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `student_id` bigint NOT NULL COMMENT '评价人ID(学生)',
  `teaching_quality` decimal(3,2) DEFAULT NULL COMMENT '教学质量评分(1-5分)',
  `content_quality` decimal(3,2) DEFAULT NULL COMMENT '内容质量评分(1-5分)',
  `interaction_score` decimal(3,2) DEFAULT NULL COMMENT '互动评分(1-5分)',
  `resource_quality` decimal(3,2) DEFAULT NULL COMMENT '资源质量评分(1-5分)',
  `overall_score` decimal(3,2) DEFAULT NULL COMMENT '总体评分(1-5分)',
  `improvement_suggest` text COMMENT '改进建议',
  `evaluate_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`evaluation_id`),
  UNIQUE KEY `idx_course_teacher_student` (`course_id`, `teacher_id`, `student_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `fk_evaluation_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluation_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluation_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='教学评价表';

-- 教学分析报告表
DROP TABLE IF EXISTS `stat_teaching_report`;
CREATE TABLE `stat_teaching_report` (
  `report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '教师ID(可选)',
  `report_type` varchar(20) NOT NULL COMMENT '报告类型(course课程 teacher教师 student_group学生群体)',
  `report_period` varchar(20) NOT NULL COMMENT '报告周期(weekly周 monthly月 term学期)',
  `report_data` json DEFAULT NULL COMMENT '报告数据(JSON)',
  `insights` varchar(2000) DEFAULT NULL COMMENT '教学洞察(JSON)',
  `improvement_areas` varchar(2000) DEFAULT NULL COMMENT '改进领域(JSON)',
  `generate_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  PRIMARY KEY (`report_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  CONSTRAINT `fk_report_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_report_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='教学分析报告表'; 