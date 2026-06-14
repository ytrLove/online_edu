/*
 数据库表结构更新脚本 - exam_question_bank表
 将subject_id字段修改为course_id字段
*/

-- 设置外键检查为0（关闭外键约束检查）
SET FOREIGN_KEY_CHECKS = 0;

-- 获取约束名称并删除与subject_id相关的外键约束（如果存在）
-- MySQL中需要先检查外键约束是否存在，然后再删除
SELECT @constraint_name := CONSTRAINT_NAME 
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'exam_question_bank' 
AND COLUMN_NAME = 'subject_id' 
AND REFERENCED_TABLE_NAME IS NOT NULL;

SET @drop_fk_query = IF(@constraint_name IS NOT NULL, 
                       CONCAT('ALTER TABLE `exam_question_bank` DROP FOREIGN KEY `', @constraint_name, '`'), 
                       'SELECT 1');
PREPARE stmt FROM @drop_fk_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 删除与subject_id相关的索引（如果存在）
-- 检查索引是否存在
SELECT @index_exists := COUNT(1) 
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'exam_question_bank' 
AND INDEX_NAME = 'idx_subject_id';

SET @drop_idx_query = IF(@index_exists > 0, 
                        'ALTER TABLE `exam_question_bank` DROP INDEX `idx_subject_id`', 
                        'SELECT 1');
PREPARE stmt FROM @drop_idx_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查course_id字段是否已存在
SELECT @column_exists := COUNT(1) 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'exam_question_bank' 
AND COLUMN_NAME = 'course_id';

-- 添加course_id字段（如果不存在）
SET @add_column_query = IF(@column_exists = 0, 
                         'ALTER TABLE `exam_question_bank` ADD COLUMN `course_id` bigint NULL DEFAULT NULL COMMENT "课程ID" AFTER `difficulty`', 
                         'SELECT 1');
PREPARE stmt FROM @add_column_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建course_id字段的索引（如果不存在）
SELECT @idx_exists := COUNT(1) 
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'exam_question_bank' 
AND INDEX_NAME = 'idx_course_id';

SET @add_idx_query = IF(@idx_exists = 0, 
                       'ALTER TABLE `exam_question_bank` ADD INDEX `idx_course_id`(`course_id` ASC)', 
                       'SELECT 1');
PREPARE stmt FROM @add_idx_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加course_id的外键约束，引用edu_course表的course_id字段
-- 检查外键约束是否存在
SELECT @fk_exists := COUNT(1) 
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'exam_question_bank' 
AND COLUMN_NAME = 'course_id' 
AND CONSTRAINT_NAME = 'fk_question_course';

SET @add_fk_query = IF(@fk_exists = 0, 
                      'ALTER TABLE `exam_question_bank` ADD CONSTRAINT `fk_question_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE SET NULL ON UPDATE CASCADE', 
                      'SELECT 1');
PREPARE stmt FROM @add_fk_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 如果同时存在subject_id和course_id字段，则将数据从subject_id复制到course_id
-- 注意：此步骤假设subject_id与course_id值相匹配，如果不是，需要根据实际情况调整
SELECT @subject_column_exists := COUNT(1) 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'exam_question_bank' 
AND COLUMN_NAME = 'subject_id';

SET @update_data_query = IF(@subject_column_exists > 0 AND @column_exists > 0, 
                          'UPDATE `exam_question_bank` SET `course_id` = `subject_id` WHERE `course_id` IS NULL', 
                          'SELECT 1');
PREPARE stmt FROM @update_data_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 删除subject_id字段（如果存在）
SET @drop_column_query = IF(@subject_column_exists > 0, 
                           'ALTER TABLE `exam_question_bank` DROP COLUMN `subject_id`', 
                           'SELECT 1');
PREPARE stmt FROM @drop_column_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 重新启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- 更新完成 