/*
 Navicat Premium Data Transfer

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : new_online_edu

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 11/05/2025 15:30:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comm_discussion_forum
-- ----------------------------
DROP TABLE IF EXISTS `comm_discussion_forum`;
CREATE TABLE `comm_discussion_forum`  (
  `forum_id` bigint NOT NULL AUTO_INCREMENT COMMENT '论坛ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `forum_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '论坛名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `is_public` int NULL DEFAULT 0 COMMENT '是否公开（0否 1是）',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`forum_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_forum_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '讨论区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comm_discussion_reply
-- ----------------------------
DROP TABLE IF EXISTS `comm_discussion_reply`;
CREATE TABLE `comm_discussion_reply`  (
  `reply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `topic_id` bigint NOT NULL COMMENT '主题ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '创建者ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父回复ID',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`reply_id`) USING BTREE,
  INDEX `idx_topic_id`(`topic_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_reply_parent` FOREIGN KEY (`parent_id`) REFERENCES `comm_discussion_reply` (`reply_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_reply_topic` FOREIGN KEY (`topic_id`) REFERENCES `comm_discussion_topic` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '讨论回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comm_discussion_topic
-- ----------------------------
DROP TABLE IF EXISTS `comm_discussion_topic`;
CREATE TABLE `comm_discussion_topic`  (
  `topic_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `forum_id` bigint NOT NULL COMMENT '论坛ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `user_id` bigint NOT NULL COMMENT '创建者ID',
  `last_reply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后回复时间',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览数',
  `reply_count` int NULL DEFAULT 0 COMMENT '回复数',
  `is_pinned` int NULL DEFAULT 0 COMMENT '是否置顶（0否 1是）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'normal' COMMENT '状态（normal正常 hidden隐藏）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`topic_id`) USING BTREE,
  INDEX `idx_forum_id`(`forum_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_topic_forum` FOREIGN KEY (`forum_id`) REFERENCES `comm_discussion_forum` (`forum_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_topic_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '讨论主题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_course
-- ----------------------------
DROP TABLE IF EXISTS `edu_course`;
CREATE TABLE `edu_course`  (
  `course_id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `course_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程代码',
  `subject_id` bigint NULL DEFAULT NULL COMMENT '学科ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程描述',
  `objectives` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教学目标',
  `syllabus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教学大纲',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '状态（draft草稿 published已发布 closed已关闭）',
  `enrollment_start_time` datetime NULL DEFAULT NULL COMMENT '选课开始时间',
  `enrollment_end_time` datetime NULL DEFAULT NULL COMMENT '选课结束时间',
  `enrollment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'upcoming' COMMENT '选课状态（open已开放 closed已关闭 upcoming即将开始）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`course_id`) USING BTREE,
  INDEX `idx_subject_id`(`subject_id` ASC) USING BTREE,
  CONSTRAINT `fk_course_subject` FOREIGN KEY (`subject_id`) REFERENCES `edu_subject` (`subject_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 507 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_course_student
-- ----------------------------
DROP TABLE IF EXISTS `edu_course_student`;
CREATE TABLE `edu_course_student`  (
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `enrollment_date` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `completion_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'in_progress' COMMENT '完成状态（not_started未开始 in_progress进行中 completed已完成）',
  `final_grade` decimal(5, 2) NULL DEFAULT NULL COMMENT '最终成绩',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`course_id`, `student_id`) USING BTREE,
  INDEX `fk_cs_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_cs_course_id` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cs_student_id` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程学生关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_course_teacher
-- ----------------------------
DROP TABLE IF EXISTS `edu_course_teacher`;
CREATE TABLE `edu_course_teacher`  (
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'main' COMMENT '角色（main主讲 assistant助教）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`course_id`, `teacher_id`) USING BTREE,
  INDEX `fk_ct_teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `fk_ct_course_id` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ct_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程教师关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_resource_type
-- ----------------------------
DROP TABLE IF EXISTS `edu_resource_type`;
CREATE TABLE `edu_resource_type`  (
  `type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `file_extensions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件扩展名',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资源类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_student
-- ----------------------------
DROP TABLE IF EXISTS `edu_student`;
CREATE TABLE `edu_student`  (
  `student_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `major` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级',
  `admission_date` date NULL DEFAULT NULL COMMENT '入学日期',
  `graduation_date` date NULL DEFAULT NULL COMMENT '预计毕业日期',
  `student_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active' COMMENT '学籍状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`student_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_subject
-- ----------------------------
DROP TABLE IF EXISTS `edu_subject`;
CREATE TABLE `edu_subject`  (
  `subject_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学科ID',
  `subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学科名称',
  `subject_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学科代码',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父学科ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`subject_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学科表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_teacher
-- ----------------------------
DROP TABLE IF EXISTS `edu_teacher`;
CREATE TABLE `edu_teacher`  (
  `teacher_id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `teacher_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工号',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属院系',
  `research_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '研究方向',
  `education` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学历',
  `degree` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学位',
  `teaching_years` int NULL DEFAULT NULL COMMENT '教龄',
  `teacher_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active' COMMENT '教师状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`teacher_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for edu_teaching_resource
-- ----------------------------
DROP TABLE IF EXISTS `edu_teaching_resource`;
CREATE TABLE `edu_teaching_resource`  (
  `resource_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称',
  `resource_type_id` bigint NULL DEFAULT NULL COMMENT '资源类型ID',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件地址',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(byte)',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '上传者ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `is_public` int NULL DEFAULT 0 COMMENT '是否公开（0否 1是）',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`resource_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_resource_type_id`(`resource_type_id` ASC) USING BTREE,
  CONSTRAINT `fk_resource_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_resource_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_resource_type` FOREIGN KEY (`resource_type_id`) REFERENCES `edu_resource_type` (`type_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_answer
-- ----------------------------
DROP TABLE IF EXISTS `exam_answer`;
CREATE TABLE `exam_answer`  (
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`, `question_id`) USING BTREE,
  INDEX `fk_ea_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_ea_question_id` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ea_record_id` FOREIGN KEY (`record_id`) REFERENCES `exam_record` (`record_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试答案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_assignment
-- ----------------------------
DROP TABLE IF EXISTS `exam_assignment`;
CREATE TABLE `exam_assignment`  (
  `assignment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `due_time` datetime NULL DEFAULT NULL COMMENT '截止时间',
  `total_points` decimal(5, 2) NULL DEFAULT 100.00 COMMENT '总分',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`assignment_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `fk_assignment_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_assignment_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_assignment_answer
-- ----------------------------
DROP TABLE IF EXISTS `exam_assignment_answer`;
CREATE TABLE `exam_assignment_answer`  (
  `submission_id` bigint NOT NULL COMMENT '提交ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `student_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生答案',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '得分',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '反馈',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`submission_id`, `question_id`) USING BTREE,
  INDEX `fk_answer_question`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_answer_submission` FOREIGN KEY (`submission_id`) REFERENCES `exam_assignment_submission` (`submission_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业答案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_assignment_question
-- ----------------------------
DROP TABLE IF EXISTS `exam_assignment_question`;
CREATE TABLE `exam_assignment_question`  (
  `assignment_id` bigint NOT NULL COMMENT '作业ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int NULL DEFAULT 0 COMMENT '题目顺序',
  `points` decimal(5, 2) NULL DEFAULT 10.00 COMMENT '分值',
  PRIMARY KEY (`assignment_id`, `question_id`) USING BTREE,
  INDEX `fk_aq_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_aq_assignment_id` FOREIGN KEY (`assignment_id`) REFERENCES `exam_assignment` (`assignment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_aq_question_id` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业题目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_assignment_submission
-- ----------------------------
DROP TABLE IF EXISTS `exam_assignment_submission`;
CREATE TABLE `exam_assignment_submission`  (
  `submission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `assignment_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `submission_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'submitted' COMMENT '状态（submitted已提交 grading评分中 graded已评分）',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总分',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '反馈',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`submission_id`) USING BTREE,
  INDEX `idx_assignment_id`(`assignment_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_submission_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `exam_assignment` (`assignment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_submission_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业提交表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_paper
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper`;
CREATE TABLE `exam_paper`  (
  `exam_id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration` int NULL DEFAULT 120 COMMENT '考试时长(分钟)',
  `total_points` decimal(5, 2) NULL DEFAULT 100.00 COMMENT '总分',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`exam_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `fk_paper_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_paper_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1021 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_paper_question
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper_question`;
CREATE TABLE `exam_paper_question`  (
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int NULL DEFAULT 0 COMMENT '题目顺序',
  `points` decimal(5, 2) NULL DEFAULT 10.00 COMMENT '分值',
  PRIMARY KEY (`exam_id`, `question_id`) USING BTREE,
  INDEX `fk_pq_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `fk_pq_exam_id` FOREIGN KEY (`exam_id`) REFERENCES `exam_paper` (`exam_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_pq_question_id` FOREIGN KEY (`question_id`) REFERENCES `exam_question_bank` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷题目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_question_bank
-- ----------------------------
DROP TABLE IF EXISTS `exam_question_bank`;
CREATE TABLE `exam_question_bank`  (
  `question_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `question_type_id` bigint NULL DEFAULT NULL COMMENT '题目类型ID',
  `question_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选项（JSON格式）',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正确答案',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '解析',
  `difficulty` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'medium' COMMENT '难度（easy简单 medium中等 hard困难）',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `idx_question_type_id`(`question_type_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_course_id` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_question_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_question_type` FOREIGN KEY (`question_type_id`) REFERENCES `exam_question_type` (`type_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10026 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题库表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_question_type
-- ----------------------------
DROP TABLE IF EXISTS `exam_question_type`;
CREATE TABLE `exam_question_type`  (
  `type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `is_auto_graded` int NULL DEFAULT 1 COMMENT '是否自动评分（0否 1是）',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_record
-- ----------------------------
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'not_started' COMMENT '状态（not_started未开始 in_progress进行中 submitted已提交 graded已评分）',
  `total_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '总得分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  UNIQUE INDEX `idx_exam_student`(`exam_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_record_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam_paper` (`exam_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_record_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_learning_behavior
-- ----------------------------
DROP TABLE IF EXISTS `stat_learning_behavior`;
CREATE TABLE `stat_learning_behavior`  (
  `behavior_id` bigint NOT NULL AUTO_INCREMENT COMMENT '行为ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `behavior_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行为类型(view_resource查看资源 submit_assignment提交作业 take_exam参加考试 post_discussion发表讨论)',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '资源ID',
  `duration` int NULL DEFAULT NULL COMMENT '持续时间(秒)',
  `behavior_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  `client_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`behavior_id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_behavior_time`(`behavior_time` ASC) USING BTREE,
  CONSTRAINT `fk_behavior_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_behavior_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习行为记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_learning_summary
-- ----------------------------
DROP TABLE IF EXISTS `stat_learning_summary`;
CREATE TABLE `stat_learning_summary`  (
  `summary_id` bigint NOT NULL AUTO_INCREMENT COMMENT '概要ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `total_study_time` int NULL DEFAULT 0 COMMENT '总学习时长(分钟)',
  `resource_access_count` int NULL DEFAULT 0 COMMENT '资源访问次数',
  `assignment_completion` int NULL DEFAULT 0 COMMENT '作业完成率(%)',
  `exam_avg_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '考试平均分',
  `discussion_count` int NULL DEFAULT 0 COMMENT '讨论参与次数',
  `last_active_time` datetime NULL DEFAULT NULL COMMENT '最后活跃时间',
  `knowledge_mastery` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '知识掌握情况(JSON)',
  `interest_points` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '兴趣点数据(JSON)',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`summary_id`) USING BTREE,
  UNIQUE INDEX `idx_student_course`(`student_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_summary_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_summary_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习概要表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_teaching_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `stat_teaching_evaluation`;
CREATE TABLE `stat_teaching_evaluation`  (
  `evaluation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `student_id` bigint NOT NULL COMMENT '评价人ID(学生)',
  `teaching_quality` decimal(3, 2) NULL DEFAULT NULL COMMENT '教学质量评分(1-5分)',
  `content_quality` decimal(3, 2) NULL DEFAULT NULL COMMENT '内容质量评分(1-5分)',
  `interaction_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '互动评分(1-5分)',
  `resource_quality` decimal(3, 2) NULL DEFAULT NULL COMMENT '资源质量评分(1-5分)',
  `overall_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '总体评分(1-5分)',
  `improvement_suggest` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '改进建议',
  `evaluate_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`evaluation_id`) USING BTREE,
  UNIQUE INDEX `idx_course_teacher_student`(`course_id` ASC, `teacher_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `fk_evaluation_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluation_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_evaluation_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_teaching_report
-- ----------------------------
DROP TABLE IF EXISTS `stat_teaching_report`;
CREATE TABLE `stat_teaching_report`  (
  `report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '教师ID(可选)',
  `report_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报告类型(course课程 teacher教师 student_group学生群体)',
  `report_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报告周期(weekly周 monthly月 term学期)',
  `report_data` json NULL COMMENT '报告数据(JSON)',
  `insights` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教学洞察(JSON)',
  `improvement_areas` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '改进领域(JSON)',
  `generate_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `fk_report_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_report_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教学分析报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` int NULL DEFAULT 1 COMMENT '菜单状态（1显示 0隐藏）',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `is_button` tinyint(1) NULL DEFAULT 0 COMMENT '是否按钮',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5547 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` int NULL DEFAULT 1 COMMENT '角色状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `idx_role_key`(`role_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `user_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'common' COMMENT '用户类型（admin管理员 teacher教师 student学生 common普通用户）',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 210 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
