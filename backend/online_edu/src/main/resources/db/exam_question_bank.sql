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

 Date: 04/05/2025 19:14:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `fk_question_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_question_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_question_type` FOREIGN KEY (`question_type_id`) REFERENCES `exam_question_type` (`type_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10023 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题库表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
