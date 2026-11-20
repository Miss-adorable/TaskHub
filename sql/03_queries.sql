-- 查询用户1的TODO任务，按ID降序排列
SELECT id, title, status
FROM tasks
WHERE user_id = 1
  AND status = 'TODO'
ORDER BY id DESC;

-- 查询用户1的HIGH或MEDIUM优先级任务，按ID升序排列
SELECT id, title, priority
FROM tasks
WHERE user_id = 1
  AND priority IN ('HIGH', 'MEDIUM')
ORDER BY id ASC;

-- 查询标题中包含 Java 的任务，按 ID 降序排列
SELECT id, title
FROM tasks
WHERE title LIKE '%Java%'
ORDER BY id DESC;

-- 查询用户1未设置截止日期的任务
SELECT id, title, due_date
FROM tasks
WHERE user_id = 1
  AND due_date IS NULL
ORDER BY id ASC;

-- 查询已设置截止日期的任务，按照截止日期升序排列
SELECT id, title, due_date
FROM tasks
WHERE due_date IS NOT NULL
ORDER BY due_date ASC;

-- 查询第2页TODO任务，每页最多2条，按ID降序
SELECT id, title, status
FROM tasks
WHERE status = 'TODO'
ORDER BY id DESC
    LIMIT 2 OFFSET 2;