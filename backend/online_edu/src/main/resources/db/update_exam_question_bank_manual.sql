/*
 数据库表结构更新脚本 - exam_question_bank表（手动分步执行版本）
 将subject_id字段修改为course_id字段
 
 注意：每个步骤可以单独执行，请按顺序执行
*/

-- 步骤1: 关闭外键约束检查
SET FOREIGN_KEY_CHECKS = 0;

-- 步骤2: 添加新字段course_id（如果已存在则注释掉此语句）
ALTER TABLE `exam_question_bank` ADD COLUMN `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID' AFTER `difficulty`;

-- 步骤3: 将subject_id的数据复制到course_id（执行前请确认course_id字段已添加成功）
UPDATE `exam_question_bank` SET `course_id` = `subject_id`;

-- 步骤4: 为course_id创建索引（如果已存在则注释掉此语句）
ALTER TABLE `exam_question_bank` ADD INDEX `idx_course_id`(`course_id` ASC);

-- 步骤5: 删除原来的subject_id字段（执行前请确认数据已成功迁移到course_id）
ALTER TABLE `exam_question_bank` DROP COLUMN `subject_id`;

-- 步骤6: 添加外键约束（如果无需外键约束则可跳过此步骤）
-- 注意：执行此步骤前请确保edu_course表存在且course_id是其主键
ALTER TABLE `exam_question_bank` ADD CONSTRAINT `fk_question_course` 
FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- 步骤7: 重新启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- 更新完成 