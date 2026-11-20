-- 初始化 TaskHub 测试用户
INSERT INTO users (id, username, display_name)
VALUES
    (1, 'java001', '张三'),
    (2, 'java002', '李四');

-- 初始化 TaskHub 测试任务
INSERT INTO tasks
(id, user_id, title, priority, status, due_date)
VALUES
    (101, 1, '学习 Java', 'HIGH', 'TODO', NULL),
    (102, 1, '阅读论文', 'MEDIUM', 'IN_PROGRESS', '2026-11-15'),
    (103, 2, '学习 MySQL', 'LOW', 'DONE', NULL),
    (104, 2, '完成实验', 'HIGH', 'TODO', NULL),
(105, 1, '学习 Spring Boot', 'MEDIUM', 'TODO', '2026-11-20'),
(106, 2, '复习 MySQL', 'LOW', 'DONE', NULL);