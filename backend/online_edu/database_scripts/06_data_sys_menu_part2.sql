-- ----------------------------
-- 菜单表数据（第二部分：课程管理）
-- ----------------------------

-- 课程管理主菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(2000, '课程管理', 0, 2, 'course', NULL, '', NULL, 'education', 'M', 1, 1, NOW(), '课程管理目录', NULL, NULL, 0);

-- 课程管理子菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
-- 学科管理
(2100, '学科管理', 2000, 1, 'subject', 'course/subject/index', '', 'edu:subject:list', 'tree', 'C', 1, 1, NOW(), '学科管理菜单', 'edu:subject:list', 'MENU', 0),
-- 课程列表
(2200, '课程列表', 2000, 2, 'list', 'course/list/index', '', 'edu:course:list', 'list', 'C', 1, 1, NOW(), '课程列表菜单', 'edu:course:list', 'MENU', 0),
-- 我的课程（教师）
(2300, '我的课程', 2000, 3, 'mycourse', 'course/mycourse/index', '', 'edu:course:mycourse', 'guide', 'C', 1, 1, NOW(), '教师课程管理', 'edu:course:mycourse', 'MENU', 0),
-- 选课管理（学生）
(2400, '选课管理', 2000, 4, 'selection', 'course/selection/index', '', 'edu:course:selection', 'form', 'C', 1, 1, NOW(), '学生选课管理', 'edu:course:selection', 'MENU', 0);

-- 学科管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(2101, '学科查询', 2100, 1, '', '', '', 'edu:subject:query', '#', 'F', 1, 1, NOW(), '', 'edu:subject:query', 'BUTTON', 1),
(2102, '学科新增', 2100, 2, '', '', '', 'edu:subject:add', '#', 'F', 1, 1, NOW(), '', 'edu:subject:add', 'BUTTON', 1),
(2103, '学科修改', 2100, 3, '', '', '', 'edu:subject:edit', '#', 'F', 1, 1, NOW(), '', 'edu:subject:edit', 'BUTTON', 1),
(2104, '学科删除', 2100, 4, '', '', '', 'edu:subject:remove', '#', 'F', 1, 1, NOW(), '', 'edu:subject:remove', 'BUTTON', 1);

-- 课程列表按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(2201, '课程查询', 2200, 1, '', '', '', 'edu:course:query', '#', 'F', 1, 1, NOW(), '', 'edu:course:query', 'BUTTON', 1),
(2202, '课程详情', 2200, 2, '', '', '', 'edu:course:detail', '#', 'F', 1, 1, NOW(), '', 'edu:course:detail', 'BUTTON', 1),
(2203, '学生列表', 2200, 3, '', '', '', 'edu:course:students', '#', 'F', 1, 1, NOW(), '', 'edu:course:students', 'BUTTON', 1);

-- 我的课程按钮（教师）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(2301, '课程查询', 2300, 1, '', '', '', 'edu:course:teacher:query', '#', 'F', 1, 1, NOW(), '', 'edu:course:teacher:query', 'BUTTON', 1),
(2302, '课程创建', 2300, 2, '', '', '', 'edu:course:create', '#', 'F', 1, 1, NOW(), '', 'edu:course:create', 'BUTTON', 1),
(2303, '课程修改', 2300, 3, '', '', '', 'edu:course:edit', '#', 'F', 1, 1, NOW(), '', 'edu:course:edit', 'BUTTON', 1),
(2304, '课程删除', 2300, 4, '', '', '', 'edu:course:remove', '#', 'F', 1, 1, NOW(), '', 'edu:course:remove', 'BUTTON', 1),
(2305, '学生管理', 2300, 5, '', '', '', 'edu:course:student:manage', '#', 'F', 1, 1, NOW(), '', 'edu:course:student:manage', 'BUTTON', 1);

-- 选课管理按钮（学生）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(2401, '可选课程查询', 2400, 1, '', '', '', 'edu:course:available', '#', 'F', 1, 1, NOW(), '', 'edu:course:available', 'BUTTON', 1),
(2402, '选课', 2400, 2, '', '', '', 'edu:course:select', '#', 'F', 1, 1, NOW(), '', 'edu:course:select', 'BUTTON', 1),
(2403, '退课', 2400, 3, '', '', '', 'edu:course:quit', '#', 'F', 1, 1, NOW(), '', 'edu:course:quit', 'BUTTON', 1),
(2404, '查看已选课程', 2400, 4, '', '', '', 'edu:course:selected', '#', 'F', 1, 1, NOW(), '', 'edu:course:selected', 'BUTTON', 1); 