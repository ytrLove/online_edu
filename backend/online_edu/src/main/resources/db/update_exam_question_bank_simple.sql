/*
 数据库表结构更新脚本 - exam_question_bank表（简化版）
 将subject_id字段修改为course_id字段
*/

-- 设置外键检查为0（关闭外键约束检查）
SET FOREIGN_KEY_CHECKS = 0;

-- 修改表结构
-- 1. 添加新字段course_id
ALTER TABLE `exam_question_bank` ADD COLUMN `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID' AFTER `difficulty`;

-- 2. 将subject_id的数据复制到course_id
UPDATE `exam_question_bank` SET `course_id` = `subject_id`;

-- 3. 为course_id创建索引
ALTER TABLE `exam_question_bank` ADD INDEX `idx_course_id`(`course_id` ASC);

-- 4. 删除原来的subject_id字段
ALTER TABLE `exam_question_bank` DROP COLUMN `subject_id`;

-- 5. 添加外键约束
-- (注意：如果edu_course表不存在或course_id不是主键，则需要调整或移除此步骤)
ALTER TABLE `exam_question_bank` ADD CONSTRAINT `fk_question_course` 
FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- 重新启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- 更新完成 