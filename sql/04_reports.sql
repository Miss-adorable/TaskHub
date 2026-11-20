-- 报表1：统计每个用户的任务数量
SELECT user_id, COUNT(*) AS task_count
FROM tasks
GROUP BY user_id;