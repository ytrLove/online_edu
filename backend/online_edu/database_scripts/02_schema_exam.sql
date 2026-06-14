-- ----------------------------
-- 4. 作业与考试模块
-- ----------------------------

-- 题目类型表
DROP TABLE IF EXISTS `exam_question_type`;
CREATE TABLE `exam_question_type` (
  `type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) NOT NULL COMMENT '类型名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `is_auto_graded` int DEFAULT 1 COMMENT '是否自动评分（0否 1是）',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='题目类型表';

-- 题库表
DROP TABLE IF EXISTS `exam_question_bank`;
CREATE TABLE `exam_question_bank` (
  `question_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `question_type_id` bigint DEFAULT NULL COMMENT '题目类型ID',
  `question_content` text NOT NULL COMMENT '题目内容',
  `options` text COMMENT '选项（JSON格式）',
  `correct_answer` text COMMENT '正确答案',
  `explanation` text COMMENT '解析',
  `difficulty` varchar(10) DEFAULT 'medium' COMMENT '难度（easy简单 medium中等 hard困难）',
  `subject_id` bigint DEFAULT NULL COMMENT '学科ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`question_id`),
  KEY `idx_question_type_id` (`question_type_id`),
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  CONSTRAINT `fk_question_type` FOREIGN KEY (`question_type_id`) REFERENCES `exam_question_type` (`type_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_question_subject` FOREIGN KEY (`subject_id`) REFERENCES `edu_subject` (`subject_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_question_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='题库表';

-- 作业表
DROP TABLE IF EXISTS `exam_assignment`;
CREATE TABLE `exam_assignment` (
  `assignment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `description` text COMMENT '描述',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `due_time` datetime DEFAULT NULL COMMENT '截止时间',
  `total_points` decimal(5,2) DEFAULT 100.00 COMMENT '总分',
  `teacher_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`assignment_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  CONSTRAINT `fk_assignment_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_assignment_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='作业表';

-- 作业题目关联表
DROP TABLE IF EXISTS `exam_assignment_question`;
CREATE TABLE `exam_assignment_question` (
  `assignment_id` bigint NOT NULL COMMENT '作业ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int DEFAULT 0 COMMENT '题目顺序',
  `points` decimal(5,2) DEFAULT 10.00 COMMENT '分值',
  PRIMARY KEY (`assignment_id`, `question_id`),
  CONSTRAINT `fk_aq_assignment_id` FOREIGN KEY (`assignment_id`) REFERENCES `exam_assignment` (`assignment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_aq_question_id` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='作业题目关联表';

-- 作业提交表
DROP TABLE IF EXISTS `exam_assignment_submission`;
CREATE TABLE `exam_assignment_submission` (
  `submission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `assignment_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `submission_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `status` varchar(20) DEFAULT 'submitted' COMMENT '状态（submitted已提交 grading评分中 graded已评分）',
  `total_score` decimal(5,2) DEFAULT NULL COMMENT '总分',
  `feedback` text COMMENT '反馈',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`submission_id`),
  KEY `idx_assignment_id` (`assignment_id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `fk_submission_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `exam_assignment` (`assignment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_submission_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='作业提交表';

-- 作业答案表
DROP TABLE IF EXISTS `exam_assignment_answer`;
CREATE TABLE `exam_assignment_answer` (
  `submission_id` bigint NOT NULL COMMENT '提交ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `student_answer` text COMMENT '学生答案',
  `score` decimal(5,2) DEFAULT NULL COMMENT '得分',
  `feedback` text COMMENT '反馈',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`submission_id`, `question_id`),
  CONSTRAINT `fk_answer_submission` FOREIGN KEY (`submission_id`) REFERENCES `exam_assignment_submission` (`submission_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='作业答案表';

-- 试卷表
DROP TABLE IF EXISTS `exam_paper`;
CREATE TABLE `exam_paper` (
  `exam_id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `description` text COMMENT '描述',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int DEFAULT 120 COMMENT '考试时长(分钟)',
  `total_points` decimal(5,2) DEFAULT 100.00 COMMENT '总分',
  `teacher_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`exam_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  CONSTRAINT `fk_paper_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_paper_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='试卷表';

-- 试卷题目关联表
DROP TABLE IF EXISTS `exam_paper_question`;
CREATE TABLE `exam_paper_question` (
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int DEFAULT 0 COMMENT '题目顺序',
  `points` decimal(5,2) DEFAULT 10.00 COMMENT '分值',
  PRIMARY KEY (`exam_id`, `question_id`),
  CONSTRAINT `fk_pq_exam_id` FOREIGN KEY (`exam_id`) REFERENCES `exam_paper` (`exam_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_pq_question_id` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='试卷题目关联表';

-- 考试记录表
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `status` varchar(20) DEFAULT 'not_started' COMMENT '状态（not_started未开始 in_progress进行中 submitted已提交 graded已评分）',
  `total_score` decimal(5,2) DEFAULT NULL COMMENT '总得分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `idx_exam_student` (`exam_id`, `student_id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `fk_record_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam_paper` (`exam_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_record_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='考试记录表';

-- 考试答案表
DROP TABLE IF EXISTS `exam_answer`;
CREATE TABLE `exam_answer` (
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `student_answer` text COMMENT '学生答案',
  `score` decimal(5,2) DEFAULT NULL COMMENT '得分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`, `question_id`),
  CONSTRAINT `fk_ea_record_id` FOREIGN KEY (`record_id`) REFERENCES `exam_record` (`record_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ea_question_id` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='考试答案表'; 