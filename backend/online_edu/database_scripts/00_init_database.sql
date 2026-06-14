-- ----------------------------
-- 初始化数据库
-- ----------------------------

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `online_edu` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE `online_edu`;

-- 创建用户并授权
-- 注意：在生产环境中，应该使用更强的密码，并限制访问IP
CREATE USER IF NOT EXISTS 'edu_user'@'localhost' IDENTIFIED BY 'edu_password';
GRANT ALL PRIVILEGES ON `online_edu`.* TO 'edu_user'@'localhost';
FLUSH PRIVILEGES; 