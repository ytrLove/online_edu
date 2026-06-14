-- ----------------------------
-- 课程相关测试数据
-- ----------------------------

-- 清空现有学科数据
DELETE FROM `edu_subject` WHERE 1=1;
ALTER TABLE `edu_subject` AUTO_INCREMENT = 1;

-- 学科数据
INSERT INTO `edu_subject` (`subject_name`, `subject_code`, `status`, `create_time`, `remark`) VALUES
('计算机科学与技术', 'CST', 1, NOW(), '计算机科学与技术学科'),
('软件工程', 'SE', 1, NOW(), '软件工程学科'),
('数据科学与大数据技术', 'DS', 1, NOW(), '数据科学与大数据技术学科'),
('人工智能', 'AI', 1, NOW(), '人工智能学科'),
('网络空间安全', 'NS', 1, NOW(), '网络空间安全学科');

-- 清空现有用户数据
DELETE FROM `edu_course_student` WHERE 1=1;
DELETE FROM `edu_course_teacher` WHERE 1=1;
DELETE FROM `edu_student` WHERE 1=1;
DELETE FROM `edu_teacher` WHERE 1=1;
DELETE FROM `edu_course` WHERE 1=1;

-- 首先插入系统用户数据（edu_teacher和edu_student依赖于sys_user表）
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `email`, `phone`, `sex`, `avatar`, `user_type`, `status`, `del_flag`, `create_time`, `update_time`, `remark`) VALUES
(101, 'teacher1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张教授', 'teacher1@example.com', '13800000001', '0', '/profile/avatar/2023/10/01/teacher1.jpg', 'teacher', 1, 0, NOW(), NOW(), '计算机科学与技术学院教授'),
(102, 'teacher2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李副教授', 'teacher2@example.com', '13800000002', '0', '/profile/avatar/2023/10/01/teacher2.jpg', 'teacher', 1, 0, NOW(), NOW(), '计算机科学与技术学院副教授'),
(103, 'teacher3', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王讲师', 'teacher3@example.com', '13800000003', '1', '/profile/avatar/2023/10/01/teacher3.jpg', 'teacher', 1, 0, NOW(), NOW(), '软件工程学院讲师'),
(104, 'teacher4', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵讲师', 'teacher4@example.com', '13800000004', '0', '/profile/avatar/2023/10/01/teacher4.jpg', 'teacher', 1, 0, NOW(), NOW(), '软件工程学院讲师'),
(105, 'teacher5', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '钱教授', 'teacher5@example.com', '13800000005', '0', '/profile/avatar/2023/10/01/teacher5.jpg', 'teacher', 1, 0, NOW(), NOW(), '数据科学学院教授'),
(106, 'teacher6', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '孙副教授', 'teacher6@example.com', '13800000006', '0', '/profile/avatar/2023/10/01/teacher6.jpg', 'teacher', 1, 0, NOW(), NOW(), '数据科学学院副教授'),
(107, 'teacher7', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '周教授', 'teacher7@example.com', '13800000007', '0', '/profile/avatar/2023/10/01/teacher7.jpg', 'teacher', 1, 0, NOW(), NOW(), '人工智能学院教授'),
(108, 'teacher8', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '吴副教授', 'teacher8@example.com', '13800000008', '1', '/profile/avatar/2023/10/01/teacher8.jpg', 'teacher', 1, 0, NOW(), NOW(), '人工智能学院副教授'),
(109, 'teacher9', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '郑教授', 'teacher9@example.com', '13800000009', '0', '/profile/avatar/2023/10/01/teacher9.jpg', 'teacher', 1, 0, NOW(), NOW(), '网络空间安全学院教授'),
(110, 'teacher10', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '冯副教授', 'teacher10@example.com', '13800000010', '0', '/profile/avatar/2023/10/01/teacher10.jpg', 'teacher', 1, 0, NOW(), NOW(), '网络空间安全学院副教授'),
(201, 'student1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张同学', 'student1@example.com', '13900000001', '0', '/profile/avatar/2023/10/01/student1.jpg', 'student', 1, 0, NOW(), NOW(), '计算机科学与技术专业学生'),
(202, 'student2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李同学', 'student2@example.com', '13900000002', '1', '/profile/avatar/2023/10/01/student2.jpg', 'student', 1, 0, NOW(), NOW(), '软件工程专业学生'),
(203, 'student3', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王同学', 'student3@example.com', '13900000003', '0', '/profile/avatar/2023/10/01/student3.jpg', 'student', 1, 0, NOW(), NOW(), '数据科学专业学生'),
(204, 'student4', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵同学', 'student4@example.com', '13900000004', '0', '/profile/avatar/2023/10/01/student4.jpg', 'student', 1, 0, NOW(), NOW(), '人工智能专业学生'),
(205, 'student5', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '钱同学', 'student5@example.com', '13900000005', '1', '/profile/avatar/2023/10/01/student5.jpg', 'student', 1, 0, NOW(), NOW(), '网络空间安全专业学生');

-- 添加教师数据
INSERT INTO `edu_teacher` (`teacher_id`, `user_id`, `teacher_no`, `title`, `department`, `research_field`, `education`, `degree`, `teaching_years`, `teacher_status`, `create_time`, `update_time`, `remark`) VALUES
(1, 101, 'T2023001', '教授', '计算机科学与技术学院', '数据结构与算法', '博士研究生', '工学博士', 10, 'active', NOW(), NOW(), '算法专家'),
(2, 102, 'T2023002', '副教授', '计算机科学与技术学院', '操作系统', '博士研究生', '工学博士', 8, 'active', NOW(), NOW(), '操作系统专家'),
(3, 103, 'T2023003', '讲师', '软件工程学院', '软件工程', '博士研究生', '工学博士', 5, 'active', NOW(), NOW(), '软件工程专家'),
(4, 104, 'T2023004', '讲师', '软件工程学院', '软件测试', '博士研究生', '工学博士', 5, 'active', NOW(), NOW(), '软件测试专家'),
(5, 105, 'T2023005', '教授', '数据科学学院', '数据库系统', '博士研究生', '理学博士', 12, 'active', NOW(), NOW(), '数据库专家'),
(6, 106, 'T2023006', '副教授', '数据科学学院', '数据挖掘', '博士研究生', '理学博士', 7, 'active', NOW(), NOW(), '数据挖掘专家'),
(7, 107, 'T2023007', '教授', '人工智能学院', '人工智能', '博士研究生', '工学博士', 15, 'active', NOW(), NOW(), '人工智能专家'),
(8, 108, 'T2023008', '副教授', '人工智能学院', '深度学习', '博士研究生', '工学博士', 6, 'active', NOW(), NOW(), '深度学习专家'),
(9, 109, 'T2023009', '教授', '网络空间安全学院', '网络安全', '博士研究生', '工学博士', 10, 'active', NOW(), NOW(), '网络安全专家'),
(10, 110, 'T2023010', '副教授', '网络空间安全学院', '系统安全', '博士研究生', '工学博士', 8, 'active', NOW(), NOW(), '系统安全专家');

-- 添加学生数据
INSERT INTO `edu_student` (`student_id`, `user_id`, `student_no`, `grade`, `major`, `class_name`, `admission_date`, `graduation_date`, `student_status`, `create_time`, `update_time`, `remark`) VALUES
(1, 201, 'S2023001', '2023级', '计算机科学与技术', '计科2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '计算机科学与技术专业学生'),
(2, 202, 'S2023002', '2023级', '软件工程', '软工2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '软件工程专业学生'),
(3, 203, 'S2023003', '2023级', '数据科学与大数据技术', '数据2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '数据科学与大数据技术专业学生'),
(4, 204, 'S2023004', '2023级', '人工智能', '人工2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '人工智能专业学生'),
(5, 205, 'S2023005', '2023级', '网络空间安全', '网安2301班', '2023-09-01', '2027-07-01', 'active', NOW(), NOW(), '网络空间安全专业学生');

-- 设置课程自增ID
ALTER TABLE `edu_course` AUTO_INCREMENT = 101;

-- 课程数据
INSERT INTO `edu_course` (`course_id`, `course_name`, `course_code`, `subject_id`, `cover_image`, `description`, `status`, `create_by`, `create_time`, `remark`) VALUES
-- 计算机科学与技术课程
(101, '数据结构与算法', 'CST101', 1, '/uploads/covers/data-structure.jpg', '本课程讲解常用数据结构和算法，包括线性表、栈、队列、树、图等数据结构，以及查找、排序等常用算法。', 'published', 1, NOW(), '计算机科学基础课程'),
(102, '操作系统原理', 'CST102', 1, '/uploads/covers/os.jpg', '本课程讲解操作系统基本原理，包括进程管理、内存管理、文件系统和设备管理等内容。', 'published', 2, NOW(), '计算机科学核心课程'),
(103, '计算机网络', 'CST103', 1, '/uploads/covers/network.jpg', '本课程讲解计算机网络基本原理，包括物理层、数据链路层、网络层、传输层和应用层的协议和实现。', 'published', 1, NOW(), '网络通信基础课程'),

-- 软件工程课程
(201, '软件工程导论', 'SE201', 2, '/uploads/covers/software-engineering.jpg', '本课程介绍软件工程的基本概念、方法和工具，包括软件生命周期、需求分析、设计、实现和测试等内容。', 'published', 3, NOW(), '软件工程入门课程'),
(202, '面向对象分析与设计', 'SE202', 2, '/uploads/covers/oop.jpg', '本课程讲解面向对象分析与设计方法，包括UML建模、设计模式和面向对象设计原则等内容。', 'published', 3, NOW(), '软件设计进阶课程'),
(203, '软件测试与质量保证', 'SE203', 2, '/uploads/covers/testing.jpg', '本课程讲解软件测试方法和质量保证，包括单元测试、集成测试、系统测试和性能测试等内容。', 'published', 4, NOW(), '软件质量保障课程'),

-- 数据科学与大数据技术课程
(301, '数据库系统原理', 'DS301', 3, '/uploads/covers/database.jpg', '本课程讲解数据库系统的基本原理，包括关系数据库模型、SQL、数据库设计和事务处理等内容。', 'published', 5, NOW(), '数据管理基础课程'),
(302, '大数据处理技术', 'DS302', 3, '/uploads/covers/bigdata.jpg', '本课程讲解大数据处理技术，包括Hadoop、Spark等分布式计算框架和大数据处理算法。', 'published', 5, NOW(), '大数据技术核心课程'),
(303, '数据挖掘', 'DS303', 3, '/uploads/covers/datamining.jpg', '本课程讲解数据挖掘的基本理论和方法，包括分类、聚类、关联规则等算法及其应用。', 'published', 6, NOW(), '数据分析进阶课程'),

-- 人工智能课程
(401, '人工智能导论', 'AI401', 4, '/uploads/covers/ai.jpg', '本课程介绍人工智能的基本概念、历史发展和主要应用领域，包括搜索算法、知识表示和推理等内容。', 'published', 7, NOW(), '人工智能入门课程'),
(402, '机器学习', 'AI402', 4, '/uploads/covers/machine-learning.jpg', '本课程讲解机器学习的基本理论和算法，包括监督学习、无监督学习和强化学习等内容。', 'published', 7, NOW(), '人工智能核心课程'),
(403, '深度学习', 'AI403', 4, '/uploads/covers/deep-learning.jpg', '本课程讲解深度学习的基本原理和方法，包括CNN、RNN、GAN等神经网络模型及其应用。', 'published', 8, NOW(), '人工智能前沿课程'),

-- 网络空间安全课程
(501, '网络安全基础', 'NS501', 5, '/uploads/covers/network-security.jpg', '本课程讲解网络安全的基本概念和技术，包括密码学、访问控制、安全协议和网络攻防等内容。', 'published', 9, NOW(), '网络安全入门课程'),
(502, '密码学与安全协议', 'NS502', 5, '/uploads/covers/cryptography.jpg', '本课程讲解现代密码学理论和安全协议，包括对称密码、公钥密码、数字签名和安全协议设计等内容。', 'published', 9, NOW(), '安全理论基础课程'),
(503, '系统安全与渗透测试', 'NS503', 5, '/uploads/covers/penetration-testing.jpg', '本课程讲解系统安全和渗透测试技术，包括漏洞分析、攻击技术和防御方法等内容。', 'published', 10, NOW(), '安全技术实战课程');

-- 课程教师关联数据
INSERT INTO `edu_course_teacher` (`course_id`, `teacher_id`, `role`, `create_time`) VALUES
-- 计算机科学与技术课程的教师
(101, 1, 'main', NOW()),
(102, 2, 'main', NOW()),
(103, 1, 'main', NOW()),
(103, 3, 'assistant', NOW()),

-- 软件工程课程的教师
(201, 3, 'main', NOW()),
(202, 3, 'main', NOW()),
(202, 4, 'assistant', NOW()),
(203, 4, 'main', NOW()),

-- 数据科学与大数据技术课程的教师
(301, 5, 'main', NOW()),
(302, 5, 'main', NOW()),
(303, 6, 'main', NOW()),

-- 人工智能课程的教师
(401, 7, 'main', NOW()),
(402, 7, 'main', NOW()),
(403, 8, 'main', NOW()),

-- 网络空间安全课程的教师
(501, 9, 'main', NOW()),
(502, 9, 'main', NOW()),
(503, 10, 'main', NOW());

-- 学生选课数据
INSERT INTO `edu_course_student` (`course_id`, `student_id`, `enrollment_date`, `completion_status`, `final_grade`, `create_time`) VALUES
-- 学生1的选课
(101, 1, NOW(), 'in_progress', NULL, NOW()),
(102, 1, NOW(), 'in_progress', NULL, NOW()),
(201, 1, NOW(), 'in_progress', NULL, NOW()),
(301, 1, NOW(), 'in_progress', NULL, NOW()),
(401, 1, NOW(), 'in_progress', NULL, NOW()),

-- 学生2的选课
(101, 2, NOW(), 'in_progress', NULL, NOW()),
(102, 2, NOW(), 'in_progress', NULL, NOW()),
(103, 2, NOW(), 'in_progress', NULL, NOW()),
(201, 2, NOW(), 'in_progress', NULL, NOW()),
(301, 2, NOW(), 'in_progress', NULL, NOW()),

-- 学生3的选课
(101, 3, NOW(), 'in_progress', NULL, NOW()),
(102, 3, NOW(), 'in_progress', NULL, NOW()),
(202, 3, NOW(), 'in_progress', NULL, NOW()),
(301, 3, NOW(), 'in_progress', NULL, NOW()),
(302, 3, NOW(), 'in_progress', NULL, NOW()),

-- 学生4的选课
(102, 4, NOW(), 'in_progress', NULL, NOW()),
(103, 4, NOW(), 'in_progress', NULL, NOW()),
(201, 4, NOW(), 'in_progress', NULL, NOW()),
(202, 4, NOW(), 'in_progress', NULL, NOW()),
(401, 4, NOW(), 'in_progress', NULL, NOW()),

-- 学生5的选课
(103, 5, NOW(), 'in_progress', NULL, NOW()),
(203, 5, NOW(), 'in_progress', NULL, NOW()),
(302, 5, NOW(), 'in_progress', NULL, NOW()),
(402, 5, NOW(), 'in_progress', NULL, NOW()),
(501, 5, NOW(), 'in_progress', NULL, NOW()); 