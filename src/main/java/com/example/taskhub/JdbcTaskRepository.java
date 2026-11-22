package com.example.taskhub;

import java.sql.*;

import java.time.LocalDate;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class JdbcTaskRepository implements TaskRepository {

    private final int userId;

    public JdbcTaskRepository(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("用户ID必须大于0");
        }

        this.userId = userId;
    }

    private Connection openConnection() throws SQLException {

        // 1. 读取环境变量
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        // 2. 检查数据库地址
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                    "缺少数据库环境变量：DB_URL"
            );
        }

        // 3. 检查数据库用户名
        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                    "缺少数据库环境变量：DB_USER"
            );
        }

        // 4. 检查数据库密码
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "缺少数据库环境变量：DB_PASSWORD"
            );
        }

        // 5. 建立数据库连接
        return DriverManager.getConnection(
                url,
                username,
                password
        );
    }

    public void checkConnection() {

        try (Connection conn = openConnection()) {

            // 连接成功，无需执行 SQL
            // try-with-resources 会自动关闭连接

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "数据库连接失败，请检查配置和 MySQL 服务状态",
                    e
            );
        }
    }

    @Override
    public Task findById(int id) {

        String sql = """
            SELECT id, title, priority, status, due_date, tags
            FROM tasks
            WHERE id = ? AND user_id = ?
            """;

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1. 绑定查询参数
            ps.setInt(1, id);
            ps.setInt(2, this.userId);

            // 2. 执行 SQL 查询
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // 3. 读取任务基本信息
                    int taskId = rs.getInt("id");
                    String title = rs.getString("title");

                    Priority priority =
                            Priority.valueOf(rs.getString("priority"));

                    TaskStatus status =
                            TaskStatus.valueOf(rs.getString("status"));

                    // 4. 读取截止日期
                    java.sql.Date sqlDate = rs.getDate("due_date");

                    LocalDate dueDate = null;

                    if (sqlDate != null) {
                        dueDate = sqlDate.toLocalDate();
                    }

                    // 5. 将 MySQL JSON 转换为 Set<String>
                    String tagsJson = rs.getString("tags");

                    Set<String> tags;

                    try {
                        ObjectMapper mapper = new ObjectMapper();

                        tags = mapper.readValue(
                                tagsJson,
                                new TypeReference<Set<String>>() {}
                        );

                    } catch (JacksonException e) {
                        throw new SQLException("解析任务标签失败", e);
                    }

                    // 6. 将数据库记录转换为 Task 对象
                    return Task.fromDatabase(
                            taskId,
                            title,
                            priority,
                            status,
                            tags,
                            dueDate
                    );
                }
            }

        } catch (SQLException e) {
            // 将受检异常转换为运行时异常，保持 TaskRepository 接口兼容
            throw new IllegalStateException("数据库查询任务失败", e);
        }

        // SQL 正常执行，但没有找到任务
        return null;
    }
    @Override
    public List<Task> findAll() {

        String sql = """
            SELECT id, title, priority, status, due_date, tags
            FROM tasks
            WHERE user_id = ?
            ORDER BY id
            """;

        List<Task> tasks = new ArrayList<>();

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, this.userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int taskId = rs.getInt("id");
                    String title = rs.getString("title");

                    Priority priority =
                            Priority.valueOf(rs.getString("priority"));

                    TaskStatus status =
                            TaskStatus.valueOf(rs.getString("status"));

                    java.sql.Date sqlDate = rs.getDate("due_date");

                    LocalDate dueDate = null;
                    if (sqlDate != null) {
                        dueDate = sqlDate.toLocalDate();
                    }

                    String tagsJson = rs.getString("tags");

                    Set<String> tags;

                    try {
                        ObjectMapper mapper = new ObjectMapper();

                        tags = mapper.readValue(
                                tagsJson,
                                new TypeReference<Set<String>>() {}
                        );

                    } catch (JacksonException e) {
                        throw new SQLException("解析任务标签失败", e);
                    }

                    Task task = Task.fromDatabase(
                            taskId,
                            title,
                            priority,
                            status,
                            tags,
                            dueDate
                    );

                    // 这里调用的是 List 集合的 add() 方法，不是 TaskRepository 的 add() 方法
                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("数据库查询任务列表失败", e);
        }

        return tasks;
    }

    @Override
    public Task add(Task task) {

        // 1. 检查传入的任务对象
        if (task == null) {
            return null;
        }

        String sql = """
            INSERT INTO tasks (
                user_id, title, priority, status, due_date, tags
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            // 2. 绑定用户ID、标题、优先级和状态
            ps.setInt(1, this.userId);
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getPriority().name());
            ps.setString(4, task.getStatus().name());

            // 3. 绑定截止日期，处理 null
            LocalDate dueDate = task.getDueDate();

            if (dueDate == null) {
                ps.setNull(5, Types.DATE);
            } else {
                ps.setDate(5, java.sql.Date.valueOf(dueDate));
            }

            // 4. 将 Set<String> 转换成 JSON 字符串
            try {
                ObjectMapper mapper = new ObjectMapper();

                String tagsJson =
                        mapper.writeValueAsString(task.getTags());

                ps.setString(6, tagsJson);

            } catch (JacksonException e) {
                throw new SQLException("序列化任务标签失败", e);
            }

            // 5. 执行 INSERT
            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException(
                        "新增任务失败，受影响行数：" + rows
                );
            }

            // 6. 获取 MySQL 自动生成的任务ID
            int newId;

            try (ResultSet keys = ps.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException(
                            "新增任务成功，但没有获取到任务ID"
                    );
                }

                newId = keys.getInt(1);
            }

            // 7. 返回与数据库ID一致的新 Task 对象
            return Task.fromDatabase(
                    newId,
                    task.getTitle(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getTags(),
                    task.getDueDate()
            );

        } catch (SQLException e) {

            // 将受检异常包装为运行时异常，保持接口兼容
            throw new IllegalStateException(
                    "数据库新增任务失败", e
            );
        }
    }


    @Override
    public boolean update(Task task) {

        // 1. 检查任务对象
        if (task == null) {
            return false;
        }

        String sql = """
            UPDATE tasks
            SET title = ?,
                priority = ?,
                status = ?,
                due_date = ?,
                tags = ?
            WHERE id = ? AND user_id = ?
            """;

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 2. 绑定标题、优先级和状态
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getPriority().name());
            ps.setString(3, task.getStatus().name());

            // 3. 绑定截止日期
            LocalDate dueDate = task.getDueDate();

            if (dueDate == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, java.sql.Date.valueOf(dueDate));
            }

            // 4. 将 Set<String> 转换成 JSON 字符串
            try {
                ObjectMapper mapper = new ObjectMapper();

                String tagsJson =
                        mapper.writeValueAsString(task.getTags());

                ps.setString(5, tagsJson);

            } catch (JacksonException e) {
                throw new SQLException("序列化任务标签失败", e);
            }

            // 5. 指定任务ID和当前用户ID
            ps.setInt(6, task.getId());
            ps.setInt(7, this.userId);

            // 6. 执行 UPDATE，判断是否成功更新一条记录
            int rows = ps.executeUpdate();

            return rows == 1;

        } catch (SQLException e) {

            // 数据库操作失败时，抛出运行时异常
            throw new IllegalStateException(
                    "数据库更新任务失败", e
            );
        }
    }

    @Override
    public boolean removeById(int id) {

        String sql = """
            DELETE FROM tasks
            WHERE id = ? AND user_id = ?
            """;

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1. 绑定任务 ID
            ps.setInt(1, id);

            // 2. 绑定当前用户 ID，防止删除其他用户的任务
            ps.setInt(2, this.userId);

            // 3. 执行 DELETE
            int rows = ps.executeUpdate();

            // 4. 只有成功删除一条任务，才返回 true
            return rows == 1;

        } catch (SQLException e) {

            // 数据库操作失败时，转换成运行时异常
            throw new IllegalStateException(
                    "数据库删除任务失败", e
            );
        }
    }

    @Override
    public boolean search(String keyword) {

        // 1. 检查搜索关键词
        if (keyword == null || keyword.isBlank()) {
            return false;
        }

        String sql = """
            SELECT id, title, priority, status, due_date, tags
            FROM tasks
            WHERE user_id = ? AND title LIKE ?
            ORDER BY id
            """;

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 2. 绑定当前用户ID和搜索关键词
            ps.setInt(1, this.userId);
            ps.setString(2, "%" + keyword + "%");

            boolean found = false;

            // 3. 执行查询
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    // 4. 读取任务基本信息
                    int taskId = rs.getInt("id");
                    String title = rs.getString("title");

                    Priority priority =
                            Priority.valueOf(rs.getString("priority"));

                    TaskStatus status =
                            TaskStatus.valueOf(rs.getString("status"));

                    // 5. 读取截止日期
                    java.sql.Date sqlDate = rs.getDate("due_date");

                    LocalDate dueDate = null;

                    if (sqlDate != null) {
                        dueDate = sqlDate.toLocalDate();
                    }

                    // 6. 将 JSON 标签转换为 Set<String>
                    String tagsJson = rs.getString("tags");

                    Set<String> tags;

                    try {
                        ObjectMapper mapper = new ObjectMapper();

                        tags = mapper.readValue(
                                tagsJson,
                                new TypeReference<Set<String>>() {}
                        );

                    } catch (JacksonException e) {
                        throw new SQLException("解析任务标签失败", e);
                    }

                    // 7. 将数据库记录转换成 Task 对象
                    Task task = Task.fromDatabase(
                            taskId,
                            title,
                            priority,
                            status,
                            tags,
                            dueDate
                    );

                    // 8. 显示匹配的任务
                    task.display();

                    found = true;
                }
            }

            return found;

        } catch (SQLException e) {

            // 将数据库异常包装成运行时异常
            throw new IllegalStateException(
                    "数据库搜索任务失败", e
            );
        }
    }
    @Override
    public void showAll() {

        // 1. 调用 findAll() 查询当前用户的全部任务
        List<Task> tasks = findAll();

        // 2. 如果没有任务，输出提示
        if (tasks.isEmpty()) {
            System.out.println("暂无任务");
            return;
        }

        // 3. 遍历任务集合并显示
        for (Task task : tasks) {
            task.display();
        }
    }

}