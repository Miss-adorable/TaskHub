-- 创建 TaskHub 用户表
CREATE TABLE IF NOT EXISTS users (
                       id INT NOT NULL AUTO_INCREMENT,
                       username VARCHAR(50) NOT NULL,
                       display_name VARCHAR(50) NULL,

                       PRIMARY KEY (id),
                       UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建 TaskHub 任务表
CREATE TABLE IF NOT EXISTS tasks (
                       id INT NOT NULL AUTO_INCREMENT,
                       user_id INT NOT NULL,
                       title VARCHAR(100) NOT NULL,
                       priority VARCHAR(10) NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'TODO',
                       due_date DATE NULL,

    -- 主键：唯一标识任务
                       PRIMARY KEY (id),

    -- 外键：任务必须关联一个存在的用户
                       CONSTRAINT fk_tasks_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;