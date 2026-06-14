-- ----------------------------
-- 1. 用户管理模块
-- ----------------------------

-- 基础用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `sex` char(1) DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像地址',
  `user_type` varchar(10) DEFAULT 'common' COMMENT '用户类型（admin管理员 teacher教师 student学生 common普通用户）',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='用户信息表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int DEFAULT 0 COMMENT '显示顺序',
  `status` int DEFAULT 1 COMMENT '角色状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `idx_role_key` (`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='角色信息表';

-- 菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` int DEFAULT 1 COMMENT '菜单状态（1显示 0隐藏）',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `permission` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `permission_type` varchar(20) DEFAULT NULL COMMENT '权限类型',
  `is_button` tinyint(1) DEFAULT 0 COMMENT '是否按钮',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='菜单权限表';

-- 用户和角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB COMMENT='用户和角色关联表';

-- 角色和菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB COMMENT='角色和菜单关联表';

-- 学生表
DROP TABLE IF EXISTS `edu_student`;
CREATE TABLE `edu_student` (
  `student_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_no` varchar(20) DEFAULT NULL COMMENT '学号',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级',
  `major` varchar(50) DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `admission_date` date DEFAULT NULL COMMENT '入学日期',
  `graduation_date` date DEFAULT NULL COMMENT '预计毕业日期',
  `student_status` varchar(20) DEFAULT 'active' COMMENT '学籍状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`student_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='学生表';

-- 教师表
DROP TABLE IF EXISTS `edu_teacher`;
CREATE TABLE `edu_teacher` (
  `teacher_id` bigint NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `teacher_no` varchar(20) DEFAULT NULL COMMENT '工号',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `department` varchar(50) DEFAULT NULL COMMENT '所属院系',
  `research_field` varchar(255) DEFAULT NULL COMMENT '研究方向',
  `education` varchar(50) DEFAULT NULL COMMENT '学历',
  `degree` varchar(50) DEFAULT NULL COMMENT '学位',
  `teaching_years` int DEFAULT NULL COMMENT '教龄',
  `teacher_status` varchar(20) DEFAULT 'active' COMMENT '教师状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`teacher_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='教师表';

-- ----------------------------
-- 2. 课程管理模块
-- ----------------------------

-- 学科表
DROP TABLE IF EXISTS `edu_subject`;
CREATE TABLE `edu_subject` (
  `subject_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学科ID',
  `subject_name` varchar(100) NOT NULL COMMENT '学科名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父学科ID',
  `order_num` int DEFAULT 0 COMMENT '显示顺序',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`subject_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='学科表';

-- 课程表
DROP TABLE IF EXISTS `edu_course`;
CREATE TABLE `edu_course` (
  `course_id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `course_code` varchar(50) DEFAULT NULL COMMENT '课程代码',
  `subject_id` bigint DEFAULT NULL COMMENT '学科ID',
  `description` text COMMENT '课程描述',
  `objectives` text COMMENT '教学目标',
  `syllabus` text COMMENT '教学大纲',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `status` varchar(20) DEFAULT 'draft' COMMENT '状态（draft草稿 published已发布 closed已关闭）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`course_id`),
  KEY `idx_subject_id` (`subject_id`),
  CONSTRAINT `fk_course_subject` FOREIGN KEY (`subject_id`) REFERENCES `edu_subject` (`subject_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='课程表';

-- 课程教师关联表
DROP TABLE IF EXISTS `edu_course_teacher`;
CREATE TABLE `edu_course_teacher` (
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `role` varchar(20) DEFAULT 'main' COMMENT '角色（main主讲 assistant助教）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`course_id`, `teacher_id`),
  CONSTRAINT `fk_ct_course_id` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ct_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='课程教师关联表';

-- 课程学生关联表
DROP TABLE IF EXISTS `edu_course_student`;
CREATE TABLE `edu_course_student` (
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `enrollment_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `completion_status` varchar(20) DEFAULT 'in_progress' COMMENT '完成状态（not_started未开始 in_progress进行中 completed已完成）',
  `final_grade` decimal(5,2) DEFAULT NULL COMMENT '最终成绩',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`course_id`, `student_id`),
  CONSTRAINT `fk_cs_course_id` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cs_student_id` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='课程学生关联表';

-- ----------------------------
-- 3. 教学资源管理模块
-- ----------------------------

-- 资源类型表
DROP TABLE IF EXISTS `edu_resource_type`;
CREATE TABLE `edu_resource_type` (
  `type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) NOT NULL COMMENT '类型名称',
  `file_extensions` varchar(255) DEFAULT NULL COMMENT '文件扩展名',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='资源类型表';

-- 教学资源表
DROP TABLE IF EXISTS `edu_teaching_resource`;
CREATE TABLE `edu_teaching_resource` (
  `resource_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name` varchar(100) NOT NULL COMMENT '资源名称',
  `resource_type_id` bigint DEFAULT NULL COMMENT '资源类型ID',
  `file_url` varchar(255) DEFAULT NULL COMMENT '文件地址',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(byte)',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '上传者ID',
  `description` text COMMENT '描述',
  `is_public` int DEFAULT 0 COMMENT '是否公开（0否 1是）',
  `download_count` int DEFAULT 0 COMMENT '下载次数',
  `status` int DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `del_flag` int DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`resource_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_resource_type_id` (`resource_type_id`),
  CONSTRAINT `fk_resource_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`course_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_resource_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`teacher_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_resource_type` FOREIGN KEY (`resource_type_id`) REFERENCES `edu_resource_type` (`type_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='教学资源表'; 