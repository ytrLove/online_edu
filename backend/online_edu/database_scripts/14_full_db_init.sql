-- ----------------------------
-- 在线教育平台数据库完整初始化脚本
-- ----------------------------

-- 初始化数据库
SOURCE 00_init_database.sql;

-- 系统和用户权限相关表结构
SOURCE 01_schema_sys_user.sql;

-- 课程管理相关表结构
SOURCE 02_schema_edu_course.sql;

-- 交互和统计分析相关表结构
SOURCE 03_schema_comm_stats.sql;

-- 系统用户和角色测试数据
SOURCE 04_data_sys_user_role.sql;

-- 菜单数据（第一部分：系统管理模块）
SOURCE 05_data_sys_menu_part1.sql;

-- 菜单数据（第二部分：课程管理模块）
SOURCE 06_data_sys_menu_part2.sql;

-- 菜单数据（第三部分：教学资源和作业考试模块）
SOURCE 07_data_sys_menu_part3.sql;

-- 菜单数据（第四部分：考试管理和交互统计模块）
SOURCE 08_data_sys_menu_part4.sql;

-- 角色-菜单关联表数据
SOURCE 09_data_sys_role_menu.sql;

-- 课程相关测试数据
SOURCE 10_data_course_test.sql;

-- 教学资源相关测试数据
SOURCE 11_data_resource_test.sql;

-- 作业考试相关测试数据
SOURCE 12_data_exam_test.sql;

-- 交互与统计相关测试数据
SOURCE 13_data_comm_stats_test.sql;

-- 提交所有修改
COMMIT; 