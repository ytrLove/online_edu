-- ----------------------------
-- 菜单表数据（第一部分：系统管理）
-- ----------------------------

-- 清空菜单和角色菜单关联表
TRUNCATE TABLE `sys_role_menu`;
DELETE FROM `sys_menu`;

-- 重置自增ID
ALTER TABLE `sys_menu` AUTO_INCREMENT = 1000;

-- 主菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
-- 系统管理
(1000, '系统管理', 0, 1, 'system', NULL, '', NULL, 'system', 'M', 1, 1, NOW(), '系统管理目录', NULL, NULL, 0),
-- 用户管理
(1100, '用户管理', 1000, 1, 'user', 'system/user/index', '', 'system:user:list', 'user', 'C', 1, 1, NOW(), '用户管理菜单', 'system:user:list', 'MENU', 0),
-- 角色管理
(1200, '角色管理', 1000, 2, 'role', 'system/role/index', '', 'system:role:list', 'peoples', 'C', 1, 1, NOW(), '角色管理菜单', 'system:role:list', 'MENU', 0),
-- 菜单管理
(1300, '菜单管理', 1000, 3, 'menu', 'system/menu/index', '', 'system:menu:list', 'tree-table', 'C', 1, 1, NOW(), '菜单管理菜单', 'system:menu:list', 'MENU', 0);

-- 用户管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(1101, '用户查询', 1100, 1, '', '', '', 'system:user:query', '#', 'F', 1, 1, NOW(), '', 'system:user:query', 'BUTTON', 1),
(1102, '用户新增', 1100, 2, '', '', '', 'system:user:add', '#', 'F', 1, 1, NOW(), '', 'system:user:add', 'BUTTON', 1),
(1103, '用户修改', 1100, 3, '', '', '', 'system:user:edit', '#', 'F', 1, 1, NOW(), '', 'system:user:edit', 'BUTTON', 1),
(1104, '用户删除', 1100, 4, '', '', '', 'system:user:remove', '#', 'F', 1, 1, NOW(), '', 'system:user:remove', 'BUTTON', 1),
(1105, '用户导出', 1100, 5, '', '', '', 'system:user:export', '#', 'F', 1, 1, NOW(), '', 'system:user:export', 'BUTTON', 1),
(1106, '重置密码', 1100, 6, '', '', '', 'system:user:resetPwd', '#', 'F', 1, 1, NOW(), '', 'system:user:resetPwd', 'BUTTON', 1);

-- 角色管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(1201, '角色查询', 1200, 1, '', '', '', 'system:role:query', '#', 'F', 1, 1, NOW(), '', 'system:role:query', 'BUTTON', 1),
(1202, '角色新增', 1200, 2, '', '', '', 'system:role:add', '#', 'F', 1, 1, NOW(), '', 'system:role:add', 'BUTTON', 1),
(1203, '角色修改', 1200, 3, '', '', '', 'system:role:edit', '#', 'F', 1, 1, NOW(), '', 'system:role:edit', 'BUTTON', 1),
(1204, '角色删除', 1200, 4, '', '', '', 'system:role:remove', '#', 'F', 1, 1, NOW(), '', 'system:role:remove', 'BUTTON', 1),
(1205, '角色导出', 1200, 5, '', '', '', 'system:role:export', '#', 'F', 1, 1, NOW(), '', 'system:role:export', 'BUTTON', 1),
(1206, '分配菜单', 1200, 6, '', '', '', 'system:role:assignMenu', '#', 'F', 1, 1, NOW(), '', 'system:role:assignMenu', 'BUTTON', 1);

-- 菜单管理按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `perms`, `icon`, `menu_type`, `visible`, `status`, `create_time`, `remark`, `permission`, `permission_type`, `is_button`) VALUES 
(1301, '菜单查询', 1300, 1, '', '', '', 'system:menu:query', '#', 'F', 1, 1, NOW(), '', 'system:menu:query', 'BUTTON', 1),
(1302, '菜单新增', 1300, 2, '', '', '', 'system:menu:add', '#', 'F', 1, 1, NOW(), '', 'system:menu:add', 'BUTTON', 1),
(1303, '菜单修改', 1300, 3, '', '', '', 'system:menu:edit', '#', 'F', 1, 1, NOW(), '', 'system:menu:edit', 'BUTTON', 1),
(1304, '菜单删除', 1300, 4, '', '', '', 'system:menu:remove', '#', 'F', 1, 1, NOW(), '', 'system:menu:remove', 'BUTTON', 1); 