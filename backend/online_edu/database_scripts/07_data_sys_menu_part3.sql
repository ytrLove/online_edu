-- ----------------------------
-- 菜单表数据（第三部分：教学资源和作业考试）
-- ----------------------------

-- 教学资源管理主菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(3000, '教学资源', 0, 3, 'resource', NULL, '', NULL, 'documentation', 'M', 1, 1, NOW(), '教学资源目录', NULL, NULL, 0);

-- 教学资源子菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
-- 资源类型
(3100, '资源类型', 3000, 1, 'type', 'resource/type/index', '', 'edu:resource:type:list', 'dict', 'C', 1, 1, NOW(), '资源类型管理', 'edu:resource:type:list', 'MENU', 0),
-- 资源管理
(3200, '资源管理', 3000, 2, 'manage', 'resource/manage/index', '', 'edu:resource:list', 'zip', 'C', 1, 1, NOW(), '资源管理菜单', 'edu:resource:list', 'MENU', 0),
-- 我的资源（教师）
(3300, '我的资源', 3000, 3, 'myresource', 'resource/myresource/index', '', 'edu:resource:my', 'build', 'C', 1, 1, NOW(), '教师资源管理', 'edu:resource:my', 'MENU', 0),
-- 课程资源（学生）
(3400, '课程资源', 3000, 4, 'courseresource', 'resource/courseresource/index', '', 'edu:resource:course', 'download', 'C', 1, 1, NOW(), '学生资源查看', 'edu:resource:course', 'MENU', 0);

-- 资源类型按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(3101, '类型查询', 3100, 1, '', '', '', 'edu:resource:type:query', '#', 'F', 1, 1, NOW(), '', 'edu:resource:type:query', 'BUTTON', 1),
(3102, '类型新增', 3100, 2, '', '', '', 'edu:resource:type:add', '#', 'F', 1, 1, NOW(), '', 'edu:resource:type:add', 'BUTTON', 1),
(3103, '类型修改', 3100, 3, '', '', '', 'edu:resource:type:edit', '#', 'F', 1, 1, NOW(), '', 'edu:resource:type:edit', 'BUTTON', 1),
(3104, '类型删除', 3100, 4, '', '', '', 'edu:resource:type:remove', '#', 'F', 1, 1, NOW(), '', 'edu:resource:type:remove', 'BUTTON', 1);

-- 资源管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(3201, '资源查询', 3200, 1, '', '', '', 'edu:resource:query', '#', 'F', 1, 1, NOW(), '', 'edu:resource:query', 'BUTTON', 1),
(3202, '资源上传', 3200, 2, '', '', '', 'edu:resource:upload', '#', 'F', 1, 1, NOW(), '', 'edu:resource:upload', 'BUTTON', 1),
(3203, '资源下载', 3200, 3, '', '', '', 'edu:resource:download', '#', 'F', 1, 1, NOW(), '', 'edu:resource:download', 'BUTTON', 1),
(3204, '资源删除', 3200, 4, '', '', '', 'edu:resource:remove', '#', 'F', 1, 1, NOW(), '', 'edu:resource:remove', 'BUTTON', 1);

-- 我的资源按钮（教师）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(3301, '我的资源查询', 3300, 1, '', '', '', 'edu:resource:my:query', '#', 'F', 1, 1, NOW(), '', 'edu:resource:my:query', 'BUTTON', 1),
(3302, '资源上传', 3300, 2, '', '', '', 'edu:resource:my:upload', '#', 'F', 1, 1, NOW(), '', 'edu:resource:my:upload', 'BUTTON', 1),
(3303, '资源修改', 3300, 3, '', '', '', 'edu:resource:my:edit', '#', 'F', 1, 1, NOW(), '', 'edu:resource:my:edit', 'BUTTON', 1),
(3304, '资源删除', 3300, 4, '', '', '', 'edu:resource:my:remove', '#', 'F', 1, 1, NOW(), '', 'edu:resource:my:remove', 'BUTTON', 1);

-- 课程资源按钮（学生）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(3401, '课程资源查询', 3400, 1, '', '', '', 'edu:resource:course:query', '#', 'F', 1, 1, NOW(), '', 'edu:resource:course:query', 'BUTTON', 1),
(3402, '资源下载', 3400, 2, '', '', '', 'edu:resource:course:download', '#', 'F', 1, 1, NOW(), '', 'edu:resource:course:download', 'BUTTON', 1);

-- 作业与考试主菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(4000, '作业考试', 0, 4, 'exam', NULL, '', NULL, 'clipboard', 'M', 1, 1, NOW(), '作业与考试目录', NULL, NULL, 0);

-- 作业与考试子菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
-- 题库管理
(4100, '题库管理', 4000, 1, 'question', 'exam/question/index', '', 'exam:question:list', 'edit', 'C', 1, 1, NOW(), '题库管理菜单', 'exam:question:list', 'MENU', 0),
-- 作业管理
(4200, '作业管理', 4000, 2, 'assignment', 'exam/assignment/index', '', 'exam:assignment:list', 'form', 'C', 1, 1, NOW(), '作业管理菜单', 'exam:assignment:list', 'MENU', 0),
-- 考试管理
(4300, '考试管理', 4000, 3, 'paper', 'exam/paper/index', '', 'exam:paper:list', 'education', 'C', 1, 1, NOW(), '考试管理菜单', 'exam:paper:list', 'MENU', 0),
-- 学生作业
(4400, '我的作业', 4000, 4, 'myassignment', 'exam/myassignment/index', '', 'exam:assignment:my', 'date', 'C', 1, 1, NOW(), '学生作业管理', 'exam:assignment:my', 'MENU', 0),
-- 学生考试
(4500, '我的考试', 4000, 5, 'myexam', 'exam/myexam/index', '', 'exam:paper:my', 'time', 'C', 1, 1, NOW(), '学生考试管理', 'exam:paper:my', 'MENU', 0);

-- 题库管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(4101, '题目查询', 4100, 1, '', '', '', 'exam:question:query', '#', 'F', 1, 1, NOW(), '', 'exam:question:query', 'BUTTON', 1),
(4102, '题目新增', 4100, 2, '', '', '', 'exam:question:add', '#', 'F', 1, 1, NOW(), '', 'exam:question:add', 'BUTTON', 1),
(4103, '题目修改', 4100, 3, '', '', '', 'exam:question:edit', '#', 'F', 1, 1, NOW(), '', 'exam:question:edit', 'BUTTON', 1),
(4104, '题目删除', 4100, 4, '', '', '', 'exam:question:remove', '#', 'F', 1, 1, NOW(), '', 'exam:question:remove', 'BUTTON', 1),
(4105, '题目导入', 4100, 5, '', '', '', 'exam:question:import', '#', 'F', 1, 1, NOW(), '', 'exam:question:import', 'BUTTON', 1),
(4106, '题目导出', 4100, 6, '', '', '', 'exam:question:export', '#', 'F', 1, 1, NOW(), '', 'exam:question:export', 'BUTTON', 1);

-- 作业管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(4201, '作业查询', 4200, 1, '', '', '', 'exam:assignment:query', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:query', 'BUTTON', 1),
(4202, '作业创建', 4200, 2, '', '', '', 'exam:assignment:create', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:create', 'BUTTON', 1),
(4203, '作业修改', 4200, 3, '', '', '', 'exam:assignment:edit', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:edit', 'BUTTON', 1),
(4204, '作业删除', 4200, 4, '', '', '', 'exam:assignment:remove', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:remove', 'BUTTON', 1),
(4205, '作业评分', 4200, 5, '', '', '', 'exam:assignment:grade', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:grade', 'BUTTON', 1),
(4206, '提交查看', 4200, 6, '', '', '', 'exam:assignment:submissions', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:submissions', 'BUTTON', 1); 