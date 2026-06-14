-- ----------------------------
-- 菜单表数据（第四部分：考试管理和交互统计）
-- ----------------------------

-- 考试管理按钮（续）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(4301, '考试查询', 4300, 1, '', '', '', 'exam:paper:query', '#', 'F', 1, 1, NOW(), '', 'exam:paper:query', 'BUTTON', 1),
(4302, '考试创建', 4300, 2, '', '', '', 'exam:paper:create', '#', 'F', 1, 1, NOW(), '', 'exam:paper:create', 'BUTTON', 1),
(4303, '考试修改', 4300, 3, '', '', '', 'exam:paper:edit', '#', 'F', 1, 1, NOW(), '', 'exam:paper:edit', 'BUTTON', 1),
(4304, '考试删除', 4300, 4, '', '', '', 'exam:paper:remove', '#', 'F', 1, 1, NOW(), '', 'exam:paper:remove', 'BUTTON', 1),
(4305, '考试评分', 4300, 5, '', '', '', 'exam:paper:grade', '#', 'F', 1, 1, NOW(), '', 'exam:paper:grade', 'BUTTON', 1),
(4306, '成绩统计', 4300, 6, '', '', '', 'exam:paper:stats', '#', 'F', 1, 1, NOW(), '', 'exam:paper:stats', 'BUTTON', 1);

-- 我的作业按钮（学生）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(4401, '作业列表', 4400, 1, '', '', '', 'exam:assignment:my:list', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:my:list', 'BUTTON', 1),
(4402, '作业提交', 4400, 2, '', '', '', 'exam:assignment:my:submit', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:my:submit', 'BUTTON', 1),
(4403, '作业查看', 4400, 3, '', '', '', 'exam:assignment:my:view', '#', 'F', 1, 1, NOW(), '', 'exam:assignment:my:view', 'BUTTON', 1);

-- 我的考试按钮（学生）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(4501, '考试列表', 4500, 1, '', '', '', 'exam:paper:my:list', '#', 'F', 1, 1, NOW(), '', 'exam:paper:my:list', 'BUTTON', 1),
(4502, '参加考试', 4500, 2, '', '', '', 'exam:paper:my:take', '#', 'F', 1, 1, NOW(), '', 'exam:paper:my:take', 'BUTTON', 1),
(4503, '考试结果', 4500, 3, '', '', '', 'exam:paper:my:result', '#', 'F', 1, 1, NOW(), '', 'exam:paper:my:result', 'BUTTON', 1);

-- 交互与统计主菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(5000, '交互统计', 0, 5, 'stat', NULL, '', NULL, 'chart', 'M', 1, 1, NOW(), '交互与统计目录', NULL, NULL, 0);

-- 交互与统计子菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
-- 讨论区管理
(5100, '讨论区管理', 5000, 1, 'forum', 'stat/forum/index', '', 'comm:forum:list', 'message', 'C', 1, 1, NOW(), '讨论区管理菜单', 'comm:forum:list', 'MENU', 0),
-- 讨论区（学生）
(5200, '讨论区', 5000, 2, 'discussion', 'stat/discussion/index', '', 'comm:discussion:list', 'chat', 'C', 1, 1, NOW(), '讨论区菜单', 'comm:discussion:list', 'MENU', 0),
-- 私信
(5300, '私信', 5000, 3, 'message', 'stat/message/index', '', 'comm:message:list', 'email', 'C', 1, 1, NOW(), '私信菜单', 'comm:message:list', 'MENU', 0),
-- 学习行为分析
(5400, '学习行为', 5000, 4, 'behavior', 'stat/behavior/index', '', 'stat:behavior:list', 'skill', 'C', 1, 1, NOW(), '学习行为菜单', 'stat:behavior:list', 'MENU', 0),
-- 教学分析报告
(5500, '教学分析', 5000, 5, 'teaching', 'stat/teaching/index', '', 'stat:teaching:list', 'chart', 'C', 1, 1, NOW(), '教学分析菜单', 'stat:teaching:list', 'MENU', 0);

-- 讨论区管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(5101, '讨论区查询', 5100, 1, '', '', '', 'comm:forum:query', '#', 'F', 1, 1, NOW(), '', 'comm:forum:query', 'BUTTON', 1),
(5102, '讨论区创建', 5100, 2, '', '', '', 'comm:forum:create', '#', 'F', 1, 1, NOW(), '', 'comm:forum:create', 'BUTTON', 1),
(5103, '讨论区修改', 5100, 3, '', '', '', 'comm:forum:edit', '#', 'F', 1, 1, NOW(), '', 'comm:forum:edit', 'BUTTON', 1),
(5104, '讨论区删除', 5100, 4, '', '', '', 'comm:forum:remove', '#', 'F', 1, 1, NOW(), '', 'comm:forum:remove', 'BUTTON', 1),
(5105, '话题管理', 5100, 5, '', '', '', 'comm:topic:manage', '#', 'F', 1, 1, NOW(), '', 'comm:topic:manage', 'BUTTON', 1);

-- 讨论区按钮（学生教师）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(5201, '讨论区列表', 5200, 1, '', '', '', 'comm:discussion:query', '#', 'F', 1, 1, NOW(), '', 'comm:discussion:query', 'BUTTON', 1),
(5202, '话题查看', 5200, 2, '', '', '', 'comm:topic:view', '#', 'F', 1, 1, NOW(), '', 'comm:topic:view', 'BUTTON', 1),
(5203, '发布话题', 5200, 3, '', '', '', 'comm:topic:create', '#', 'F', 1, 1, NOW(), '', 'comm:topic:create', 'BUTTON', 1),
(5204, '回复话题', 5200, 4, '', '', '', 'comm:reply:create', '#', 'F', 1, 1, NOW(), '', 'comm:reply:create', 'BUTTON', 1),
(5205, '删除回复', 5200, 5, '', '', '', 'comm:reply:remove', '#', 'F', 1, 1, NOW(), '', 'comm:reply:remove', 'BUTTON', 1);

-- 私信按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(5301, '私信列表', 5300, 1, '', '', '', 'comm:message:query', '#', 'F', 1, 1, NOW(), '', 'comm:message:query', 'BUTTON', 1),
(5302, '发送私信', 5300, 2, '', '', '', 'comm:message:send', '#', 'F', 1, 1, NOW(), '', 'comm:message:send', 'BUTTON', 1),
(5303, '查看私信', 5300, 3, '', '', '', 'comm:message:view', '#', 'F', 1, 1, NOW(), '', 'comm:message:view', 'BUTTON', 1),
(5304, '删除私信', 5300, 4, '', '', '', 'comm:message:remove', '#', 'F', 1, 1, NOW(), '', 'comm:message:remove', 'BUTTON', 1);

-- 学习行为分析按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(5401, '行为数据查询', 5400, 1, '', '', '', 'stat:behavior:query', '#', 'F', 1, 1, NOW(), '', 'stat:behavior:query', 'BUTTON', 1),
(5402, '学习情况总结', 5400, 2, '', '', '', 'stat:learning:summary', '#', 'F', 1, 1, NOW(), '', 'stat:learning:summary', 'BUTTON', 1),
(5403, '学习报告导出', 5400, 3, '', '', '', 'stat:learning:export', '#', 'F', 1, 1, NOW(), '', 'stat:learning:export', 'BUTTON', 1);

-- 教学分析报告按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(5501, '教学数据查询', 5500, 1, '', '', '', 'stat:teaching:query', '#', 'F', 1, 1, NOW(), '', 'stat:teaching:query', 'BUTTON', 1),
(5502, '教学评价查看', 5500, 2, '', '', '', 'stat:teaching:evaluation', '#', 'F', 1, 1, NOW(), '', 'stat:teaching:evaluation', 'BUTTON', 1),
(5503, '教学报告生成', 5500, 3, '', '', '', 'stat:teaching:report', '#', 'F', 1, 1, NOW(), '', 'stat:teaching:report', 'BUTTON', 1),
(5504, '教学报告导出', 5500, 4, '', '', '', 'stat:teaching:export', '#', 'F', 1, 1, NOW(), '', 'stat:teaching:export', 'BUTTON', 1); 