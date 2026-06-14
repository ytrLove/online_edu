-- ----------------------------
-- 用户和角色相关数据
-- ----------------------------

-- 插入系统用户
INSERT INTO `sys_user` VALUES 
(1, 'admin', '$2a$10$mrAidE.cMgt35x0TKoZab.J.NlKb9pSE0ioLKTKRcFXoKOUkRGkea', '系统管理员', 'admin@example.com', '13900000001', '0', 'https://example.com/avatar/admin.png', 'admin', 1, 0, NOW(), NOW(), '系统管理员账号'),
(2, 'teacher1', '$2a$10$mrAidE.cMgt35x0TKoZab.J.NlKb9pSE0ioLKTKRcFXoKOUkRGkea', '张教授', 'teacher1@example.com', '13900000002', '0', 'https://example.com/avatar/teacher1.png', 'teacher', 1, 0, NOW(), NOW(), '计算机科学教授'),
(3, 'teacher2', '$2a$10$mrAidE.cMgt35x0TKoZab.J.NlKb9pSE0ioLKTKRcFXoKOUkRGkea', '李讲师', 'teacher2@example.com', '13900000003', '1', 'https://example.com/avatar/teacher2.png', 'teacher', 1, 0, NOW(), NOW(), '数学系讲师'),
(4, 'student1', '$2a$10$mrAidE.cMgt35x0TKoZab.J.NlKb9pSE0ioLKTKRcFXoKOUkRGkea', '王同学', 'student1@example.com', '13900000004', '0', 'https://example.com/avatar/student1.png', 'student', 1, 0, NOW(), NOW(), '计算机专业学生'),
(5, 'student2', '$2a$10$mrAidE.cMgt35x0TKoZab.J.NlKb9pSE0ioLKTKRcFXoKOUkRGkea', '赵同学', 'student2@example.com', '13900000005', '1', 'https://example.com/avatar/student2.png', 'student', 1, 0, NOW(), NOW(), '数学专业学生'),
(6, 'student3', '$2a$10$mrAidE.cMgt35x0TKoZab.J.NlKb9pSE0ioLKTKRcFXoKOUkRGkea', '刘同学', 'student3@example.com', '13900000006', '0', 'https://example.com/avatar/student3.png', 'student', 1, 0, NOW(), NOW(), '物理专业学生');

-- 插入角色数据
INSERT INTO `sys_role` VALUES 
(1, '超级管理员', 'admin', 1, 1, 0, NOW(), NOW(), '系统最高权限'),
(2, '教师', 'teacher', 2, 1, 0, NOW(), NOW(), '教师角色'),
(3, '学生', 'student', 3, 1, 0, NOW(), NOW(), '学生角色'),
(4, '助教', 'assistant', 4, 1, 0, NOW(), NOW(), '助教角色'),
(5, '课程管理员', 'course_admin', 5, 1, 0, NOW(), NOW(), '课程管理员角色');

-- 用户角色关联
INSERT INTO `sys_user_role` VALUES 
(1, 1), -- 管理员-超级管理员
(2, 2), -- 教师1-教师
(3, 2), -- 教师2-教师
(4, 3), -- 学生1-学生
(5, 3), -- 学生2-学生
(6, 3), -- 学生3-学生
(2, 4); -- 教师1-助教

-- 插入教师数据
INSERT INTO `edu_teacher` VALUES 
(1, 2, 'T2023001', '教授', '计算机科学学院', '人工智能，机器学习', '博士', '工学博士', 10, 'active', NOW(), NOW(), '人工智能领域专家'),
(2, 3, 'T2023002', '讲师', '数学系', '线性代数，统计学', '博士', '理学博士', 5, 'active', NOW(), NOW(), '线性代数教学优秀');

-- 插入学生数据
INSERT INTO `edu_student` VALUES 
(1, 4, 'S2023001', '2023级', '计算机科学与技术', '计科2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '编程兴趣浓厚'),
(2, 5, 'S2023002', '2023级', '数学', '数学2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '数学竞赛获奖'),
(3, 6, 'S2023003', '2023级', '物理学', '物理2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '实验能力突出'); 