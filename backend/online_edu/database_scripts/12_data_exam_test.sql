-- ----------------------------
-- 考试系统相关测试数据
-- ----------------------------

-- 清空现有考试相关数据
DELETE FROM `exam_answer` WHERE 1=1;
DELETE FROM `exam_record` WHERE 1=1;
DELETE FROM `exam_paper_question` WHERE 1=1;
DELETE FROM `exam_paper` WHERE 1=1;
DELETE FROM `exam_question_bank` WHERE 1=1;
DELETE FROM `exam_question_type` WHERE 1=1;

-- 重置自增ID
ALTER TABLE `exam_paper` AUTO_INCREMENT = 1001;
ALTER TABLE `exam_question_bank` AUTO_INCREMENT = 10001;
ALTER TABLE `exam_question_type` AUTO_INCREMENT = 1;
ALTER TABLE `exam_record` AUTO_INCREMENT = 1;

-- 添加题目类型数据
INSERT INTO `exam_question_type` (`type_id`, `type_name`, `description`, `status`, `create_time`, `update_time`) VALUES
(1, '单选题', '从多个选项中选择一个正确答案', 1, NOW(), NOW()),
(2, '多选题', '从多个选项中选择多个正确答案', 1, NOW(), NOW()),
(3, '判断题', '判断题目的正确与否', 1, NOW(), NOW()),
(4, '填空题', '在空白处填写正确答案', 1, NOW(), NOW()),
(5, '简答题', '简要回答问题要点', 1, NOW(), NOW()),
(6, '编程题', '编写代码解决特定问题', 1, NOW(), NOW());

-- 添加题库数据 - 数据结构与算法相关题目
INSERT INTO `exam_question_bank` (`question_id`, `question_type_id`, `question_content`, `options`, `correct_answer`, `explanation`, `difficulty`, `subject_id`, `teacher_id`, `status`, `create_time`) VALUES
-- 单选题
(10001, 1, '以下哪种数据结构不是线性结构？', '[{"key":"A","value":"数组"},{"key":"B","value":"链表"},{"key":"C","value":"队列"},{"key":"D","value":"树"}]', 'D', '线性结构是指数据元素之间存在一对一的线性关系的数据结构，包括数组、链表、栈、队列等。树是非线性结构，其中数据元素之间存在一对多的关系。', 'easy', 1, 1, 1, NOW()),
(10002, 1, '时间复杂度为O(n²)的排序算法是？', '[{"key":"A","value":"快速排序"},{"key":"B","value":"归并排序"},{"key":"C","value":"插入排序"},{"key":"D","value":"堆排序"}]', 'C', '插入排序的平均时间复杂度为O(n²)，而快速排序、归并排序和堆排序的平均时间复杂度都是O(nlogn)。', 'medium', 1, 1, 1, NOW()),
(10003, 1, '二分查找的时间复杂度是？', '[{"key":"A","value":"O(1)"},{"key":"B","value":"O(n)"},{"key":"C","value":"O(logn)"},{"key":"D","value":"O(n²)"}]', 'C', '二分查找每次将查找范围缩小一半，因此时间复杂度为O(logn)。', 'easy', 1, 1, 1, NOW()),

-- 多选题
(10004, 2, '以下哪些排序算法是稳定的？', '[{"key":"A","value":"冒泡排序"},{"key":"B","value":"快速排序"},{"key":"C","value":"插入排序"},{"key":"D","value":"选择排序"}]', 'AC', '稳定排序算法是指相等的元素在排序前后的相对位置不变。冒泡排序和插入排序是稳定的，而快速排序和选择排序是不稳定的。', 'medium', 1, 1, 1, NOW()),
(10005, 2, '以下哪些数据结构可以用来实现优先队列？', '[{"key":"A","value":"数组"},{"key":"B","value":"链表"},{"key":"C","value":"堆"},{"key":"D","value":"二叉搜索树"}]', 'ACD', '优先队列可以用数组、堆或二叉搜索树来实现，其中堆是最常用的实现方式，因为它提供了O(logn)的插入和删除操作复杂度。', 'hard', 1, 1, 1, NOW()),

-- 判断题
(10006, 3, '快速排序的最坏时间复杂度为O(n²)。', NULL, 'true', '当输入数组已经排序或逆序排序时，如果选择第一个或最后一个元素作为枢轴，快速排序的时间复杂度会退化为O(n²)。', 'medium', 1, 1, 1, NOW()),
(10007, 3, '堆是一种完全二叉树。', NULL, 'true', '堆是一种特殊的完全二叉树，它满足堆属性：在最大堆中，父节点的值大于等于子节点的值；在最小堆中，父节点的值小于等于子节点的值。', 'easy', 1, 1, 1, NOW()),

-- 填空题
(10008, 4, '链表中的每个元素称为____。', NULL, '节点', '链表是由节点组成的线性数据结构，每个节点包含数据字段和指向下一个节点的引用。', 'easy', 1, 1, 1, NOW()),
(10009, 4, '计算斐波那契数列的递归算法的时间复杂度为____。', NULL, 'O(2^n)', '使用简单递归计算斐波那契数列的时间复杂度是指数级的，因为每个数都需要计算其前两个数的和，导致大量重复计算。', 'medium', 1, 1, 1, NOW()),

-- 简答题
(10010, 5, '简述快速排序的基本思想和实现步骤。', NULL, '快速排序是一种分治算法，其基本思想是：\n1. 选取一个基准元素（pivot）\n2. 将数组分为两部分，左边的元素都小于基准，右边的元素都大于基准\n3. 递归地对左右两部分进行快速排序\n\n实现步骤：\n1. 选择一个基准元素\n2. 从数组的两端开始向中间扫描，将小于基准的元素放到左边，大于基准的元素放到右边\n3. 递归地对左右子数组应用相同的过程', '快速排序是一种高效的排序算法，平均时间复杂度为O(nlogn)，但最坏情况下可能退化为O(n²)。选择合适的基准元素（如三数取中法）可以降低最坏情况发生的概率。', 'hard', 1, 1, 1, NOW()),

-- 编程题
(10011, 6, '实现一个函数，使用递归方法计算斐波那契数列的第n项。', NULL, 'int fibonacci(int n) {\n    if (n <= 1) return n;\n    return fibonacci(n-1) + fibonacci(n-2);\n}', '这是一个简单的递归实现，但效率很低。可以使用动态规划或记忆化搜索来优化算法，避免重复计算。', 'medium', 1, 1, 1, NOW());

-- 添加题库数据 - 操作系统相关题目
INSERT INTO `exam_question_bank` (`question_id`, `question_type_id`, `question_content`, `options`, `correct_answer`, `explanation`, `difficulty`, `subject_id`, `teacher_id`, `status`, `create_time`) VALUES
-- 单选题
(10012, 1, '以下哪种调度算法可能会导致进程饥饿？', '[{"key":"A","value":"先来先服务(FCFS)"},{"key":"B","value":"时间片轮转(RR)"},{"key":"C","value":"最短作业优先(SJF)"},{"key":"D","value":"多级反馈队列"}]', 'C', '最短作业优先调度算法会优先执行所需时间最短的作业，如果系统中不断有短作业到达，长作业可能会一直得不到处理，从而导致饥饿。', 'medium', 2, 2, 1, NOW()),

-- 判断题
(10013, 3, '分页和分段是虚拟内存管理的两种不同技术。', NULL, 'true', '分页是将物理内存和虚拟内存划分为固定大小的块，而分段是根据程序的逻辑结构划分不同长度的段。两者都是虚拟内存管理的技术，但原理和特点不同。', 'medium', 2, 2, 1, NOW()),

-- 简答题
(10014, 5, '解释死锁产生的四个必要条件，并描述如何预防死锁。', NULL, '死锁产生的四个必要条件：\n1. 互斥条件：资源不能被多个进程共享\n2. 请求与保持条件：进程已获得资源，同时又提出新的资源请求\n3. 不剥夺条件：已分配给进程的资源不能强行剥夺\n4. 循环等待条件：存在一组进程，形成循环资源等待关系\n\n预防死锁的方法：\n1. 破坏互斥条件：允许资源共享\n2. 破坏请求与保持条件：要求进程一次性申请所有资源\n3. 破坏不剥夺条件：允许资源剥夺\n4. 破坏循环等待条件：按序分配资源，规定资源获取的顺序', '死锁是操作系统中的一个经典问题，只有四个条件同时满足时才会发生。通过破坏任意一个条件可以预防死锁，但每种预防方法都有其优缺点和适用场景。', 'hard', 2, 2, 1, NOW());

-- 添加题库数据 - 数据库原理相关题目
INSERT INTO `exam_question_bank` (`question_id`, `question_type_id`, `question_content`, `options`, `correct_answer`, `explanation`, `difficulty`, `subject_id`, `teacher_id`, `status`, `create_time`) VALUES
-- 单选题
(10015, 1, '关系数据库中，关系的基本单位是什么？', '[{"key":"A","value":"元组"},{"key":"B","value":"字段"},{"key":"C","value":"码"},{"key":"D","value":"属性"}]', 'A', '关系是元组的集合，元组是关系的基本单位，对应表中的一行数据。', 'easy', 3, 5, 1, NOW()),

-- 多选题
(10016, 2, '以下哪些是SQL的DDL语句？', '[{"key":"A","value":"CREATE"},{"key":"B","value":"INSERT"},{"key":"C","value":"ALTER"},{"key":"D","value":"SELECT"}]', 'AC', 'DDL（数据定义语言）包括CREATE、ALTER、DROP等用于定义数据库结构的语句。INSERT属于DML（数据操作语言），SELECT属于DQL（数据查询语言）。', 'medium', 3, 5, 1, NOW());

-- 创建考试试卷 - 数据结构期中考试
INSERT INTO `exam_paper` (`exam_id`, `course_id`, `title`, `description`, `start_time`, `end_time`, `duration`, `total_points`, `teacher_id`, `status`, `create_time`) VALUES
(1001, 101, '数据结构与算法 - 期中考试', '本次考试涵盖线性表、排序算法、查找算法等内容，请认真作答。', '2023-10-15 09:00:00', '2023-10-15 11:00:00', 120, 100.00, 1, 1, NOW());

-- 创建考试试卷 - 操作系统期末考试
INSERT INTO `exam_paper` (`exam_id`, `course_id`, `title`, `description`, `start_time`, `end_time`, `duration`, `total_points`, `teacher_id`, `status`, `create_time`) VALUES
(1002, 102, '操作系统原理 - 期末考试', '本次考试涵盖进程管理、内存管理、文件系统等内容，请认真作答。', '2023-12-20 14:00:00', '2023-12-20 16:00:00', 120, 100.00, 2, 1, NOW());

-- 创建考试试卷 - 数据库系统测验
INSERT INTO `exam_paper` (`exam_id`, `course_id`, `title`, `description`, `start_time`, `end_time`, `duration`, `total_points`, `teacher_id`, `status`, `create_time`) VALUES
(1003, 301, '数据库系统原理 - 单元测验', '本次测验主要考察关系数据库基本概念和SQL语言使用。', '2023-11-05 10:00:00', '2023-11-05 11:30:00', 90, 60.00, 5, 1, NOW());

-- 关联试卷和题目 - 数据结构期中考试
INSERT INTO `exam_paper_question` (`exam_id`, `question_id`, `question_order`, `points`) VALUES
(1001, 10001, 1, 10.00),
(1001, 10002, 2, 10.00),
(1001, 10003, 3, 10.00),
(1001, 10004, 4, 15.00),
(1001, 10006, 5, 5.00),
(1001, 10007, 6, 5.00),
(1001, 10008, 7, 10.00),
(1001, 10010, 8, 20.00),
(1001, 10011, 9, 15.00);

-- 关联试卷和题目 - 操作系统期末考试
INSERT INTO `exam_paper_question` (`exam_id`, `question_id`, `question_order`, `points`) VALUES
(1002, 10012, 1, 20.00),
(1002, 10013, 2, 20.00),
(1002, 10014, 3, 60.00);

-- 关联试卷和题目 - 数据库系统测验
INSERT INTO `exam_paper_question` (`exam_id`, `question_id`, `question_order`, `points`) VALUES
(1003, 10015, 1, 20.00),
(1003, 10016, 2, 40.00);

-- 考试记录数据 - 学生参加考试情况
INSERT INTO `exam_record` (`record_id`, `exam_id`, `student_id`, `start_time`, `submit_time`, `status`, `total_score`, `create_time`) VALUES
(1, 1001, 1, '2023-10-15 09:05:23', '2023-10-15 10:45:18', 'graded', 85.00, NOW()),
(2, 1001, 2, '2023-10-15 09:01:45', '2023-10-15 10:58:32', 'graded', 92.00, NOW()),
(3, 1001, 3, '2023-10-15 09:10:12', '2023-10-15 10:40:56', 'graded', 78.00, NOW()),
(4, 1001, 4, '2023-10-15 09:03:28', '2023-10-15 10:30:15', 'graded', 88.00, NOW()),
(5, 1001, 5, '2023-10-15 09:08:40', '2023-10-15 10:50:22', 'graded', 90.00, NOW()),
(6, 1002, 1, '2023-12-20 14:02:18', '2023-12-20 15:45:30', 'graded', 82.00, NOW()),
(7, 1002, 2, '2023-12-20 14:00:56', '2023-12-20 15:58:20', 'graded', 95.00, NOW()),
(8, 1002, 3, '2023-12-20 14:05:45', '2023-12-20 15:40:12', 'graded', 75.00, NOW()),
(9, 1003, 1, '2023-11-05 10:01:30', '2023-11-05 11:15:45', 'graded', 50.00, NOW()),
(10, 1003, 3, '2023-11-05 10:03:22', '2023-11-05 11:20:18', 'graded', 55.00, NOW());

-- 考试答案数据 - 部分学生答案
INSERT INTO `exam_answer` (`record_id`, `question_id`, `student_answer`, `score`, `create_time`) VALUES
-- 学生1的数据结构考试答案
(1, 10001, 'D', 10.00, NOW()),
(1, 10002, 'C', 10.00, NOW()),
(1, 10003, 'C', 10.00, NOW()),
(1, 10004, 'AC', 15.00, NOW()),
(1, 10006, 'true', 5.00, NOW()),
(1, 10007, 'true', 5.00, NOW()),
(1, 10008, '结点', 8.00, NOW()),
(1, 10010, '快速排序是一种分治算法，通过选取一个基准元素将数组分为两部分，左边的元素都小于基准，右边的元素都大于基准，然后递归地对左右两部分进行快速排序。', 15.00, NOW()),
(1, 10011, 'int fibonacci(int n) {\n    if (n <= 1) return n;\n    return fibonacci(n-1) + fibonacci(n-2);\n}', 7.00, NOW()),

-- 学生1的操作系统考试答案
(6, 10012, 'C', 20.00, NOW()),
(6, 10013, 'true', 20.00, NOW()),
(6, 10014, '死锁产生的四个必要条件：互斥条件、请求与保持条件、不剥夺条件和循环等待条件。可以通过破坏任意一个条件来预防死锁。', 42.00, NOW()),

-- 学生1的数据库测验答案
(9, 10015, 'A', 20.00, NOW()),
(9, 10016, 'ACD', 30.00, NOW()); 