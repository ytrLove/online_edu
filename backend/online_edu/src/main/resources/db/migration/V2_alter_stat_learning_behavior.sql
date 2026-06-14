-- 为stat_learning_behavior表添加缺失的列
ALTER TABLE stat_learning_behavior ADD COLUMN description VARCHAR(500) COMMENT '行为描述';
ALTER TABLE stat_learning_behavior ADD COLUMN completion_rate DOUBLE COMMENT '完成率(0-1)';
ALTER TABLE stat_learning_behavior ADD COLUMN ip_address VARCHAR(50) COMMENT 'IP地址'; 