-- ----------------------------
-- 教学资源相关测试数据
-- ----------------------------

-- 首先清空现有教学资源和资源类型数据
DELETE FROM `edu_teaching_resource` WHERE 1=1;
DELETE FROM `edu_resource_type` WHERE 1=1;
ALTER TABLE `edu_resource_type` AUTO_INCREMENT = 1;
ALTER TABLE `edu_teaching_resource` AUTO_INCREMENT = 1;

-- 资源类型数据
INSERT INTO `edu_resource_type` (`type_id`, `type_name`, `file_extensions`, `icon`, `status`, `create_time`, `update_time`, `remark`) VALUES
(1, 'PPT演示文稿', 'ppt,pptx', '/static/icons/ppt.png', 1, NOW(), NOW(), '课件演示文稿'),
(2, 'Word文档', 'doc,docx', '/static/icons/word.png', 1, NOW(), NOW(), '教案和学习资料'),
(3, 'PDF文件', 'pdf', '/static/icons/pdf.png', 1, NOW(), NOW(), '电子教材和学习资料'),
(4, '视频', 'mp4,avi,mov', '/static/icons/video.png', 1, NOW(), NOW(), '教学视频资料'),
(5, '音频', 'mp3,wav,ogg', '/static/icons/audio.png', 1, NOW(), NOW(), '教学音频资料'),
(6, '图片', 'jpg,jpeg,png,gif', '/static/icons/image.png', 1, NOW(), NOW(), '教学图片资料'),
(7, '压缩包', 'zip,rar,7z', '/static/icons/zip.png', 1, NOW(), NOW(), '打包资料'),
(8, '代码文件', 'java,py,cpp,c,js,html,css', '/static/icons/code.png', 1, NOW(), NOW(), '编程示例代码');

-- 教学资源数据
INSERT INTO `edu_teaching_resource` (`resource_id`, `resource_name`, `resource_type_id`, `file_url`, `file_size`, `course_id`, `teacher_id`, `description`, `is_public`, `download_count`, `status`, `del_flag`, `create_time`, `update_time`) VALUES
-- 数据结构与算法课程资源
(1, '数据结构与算法-课程大纲', 2, '/uploads/resources/cst101/course_outline.docx', 256000, 101, 1, '本课程大纲详细介绍了课程内容、教学目标、考核方式等', 1, 45, 1, 0, NOW(), NOW()),
(2, '第1章-绪论', 1, '/uploads/resources/cst101/chapter1.pptx', 3145728, 101, 1, '数据结构与算法概述，时间复杂度和空间复杂度的分析方法', 1, 58, 1, 0, NOW(), NOW()),
(3, '第2章-线性表', 1, '/uploads/resources/cst101/chapter2.pptx', 4194304, 101, 1, '线性表的定义、基本操作及实现方式（顺序表、链表）', 1, 52, 1, 0, NOW(), NOW()),
(4, '线性表编程实例', 8, '/uploads/resources/cst101/linked_list_example.java', 15360, 101, 1, '链表实现的Java代码示例', 1, 37, 1, 0, NOW(), NOW()),

-- 操作系统原理课程资源
(5, '操作系统教材电子版', 3, '/uploads/resources/cst102/os_textbook.pdf', 15728640, 102, 2, '《现代操作系统》第4版完整电子版', 1, 78, 1, 0, NOW(), NOW()),
(6, '第1章-操作系统简介', 1, '/uploads/resources/cst102/chapter1.pptx', 5242880, 102, 2, '操作系统的概念、功能和发展历史', 1, 64, 1, 0, NOW(), NOW()),
(7, '进程管理视频讲解', 4, '/uploads/resources/cst102/process_management.mp4', 104857600, 102, 2, '详细讲解进程的创建、调度和通信机制', 1, 48, 1, 0, NOW(), NOW()),

-- 软件工程导论课程资源
(8, '软件工程导论教材', 3, '/uploads/resources/se201/se_textbook.pdf', 18874368, 201, 3, '《软件工程：实践者的研究方法》第9版', 1, 92, 1, 0, NOW(), NOW()),
(9, '软件开发过程模型', 1, '/uploads/resources/se201/sdlc.pptx', 6291456, 201, 3, '瀑布模型、增量模型、敏捷开发等软件过程模型的比较', 1, 67, 1, 0, NOW(), NOW()),
(10, '需求分析案例', 2, '/uploads/resources/se201/requirement_case.docx', 512000, 201, 3, '在线教育系统的需求分析文档示例', 1, 56, 1, 0, NOW(), NOW()),

-- 数据库系统原理课程资源
(11, 'SQL基础教程', 3, '/uploads/resources/ds301/sql_tutorial.pdf', 10485760, 301, 5, 'SQL语言详解，包括DDL、DML和DCL操作', 1, 85, 1, 0, NOW(), NOW()),
(12, '关系数据库设计', 1, '/uploads/resources/ds301/db_design.pptx', 7340032, 301, 5, '数据库设计理论、ER图和规范化理论', 1, 72, 1, 0, NOW(), NOW()),
(13, '数据库实验指导', 2, '/uploads/resources/ds301/lab_guide.docx', 768000, 301, 5, '数据库设计和查询的实验指导书', 1, 63, 1, 0, NOW(), NOW()),
(14, 'MySQL操作演示', 4, '/uploads/resources/ds301/mysql_demo.mp4', 157286400, 301, 5, 'MySQL数据库的安装、配置和基本操作演示', 1, 59, 1, 0, NOW(), NOW()),

-- 人工智能导论课程资源
(15, '人工智能历史与现状', 1, '/uploads/resources/ai401/ai_history.pptx', 8388608, 401, 7, '人工智能的发展历程和当前研究热点', 1, 96, 1, 0, NOW(), NOW()),
(16, '智能搜索算法实现', 8, '/uploads/resources/ai401/search_algorithm.py', 20480, 401, 7, '广度优先搜索、深度优先搜索、A*算法的Python实现', 1, 75, 1, 0, NOW(), NOW()),
(17, '人工智能应用案例集', 7, '/uploads/resources/ai401/ai_applications.zip', 52428800, 401, 7, '包含多个人工智能应用领域的案例分析和代码示例', 1, 88, 1, 0, NOW(), NOW()),

-- 网络安全基础课程资源
(18, '密码学基础', 1, '/uploads/resources/ns501/cryptography.pptx', 5767168, 501, 9, '对称加密、非对称加密和哈希函数介绍', 1, 78, 1, 0, NOW(), NOW()),
(19, '网络攻防实战指南', 3, '/uploads/resources/ns501/security_guide.pdf', 12582912, 501, 9, '网络安全防御策略和常见攻击手段分析', 1, 82, 1, 0, NOW(), NOW()),
(20, '安全工具使用演示', 4, '/uploads/resources/ns501/security_tools.mp4', 209715200, 501, 9, '常用网络安全工具的使用方法演示', 1, 67, 1, 0, NOW(), NOW()); 