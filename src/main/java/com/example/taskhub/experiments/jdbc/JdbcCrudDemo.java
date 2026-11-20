package com.example.taskhub.experiments.jdbc;
import com.example.taskhub.Priority;
import com.example.taskhub.Task;
import com.example.taskhub.TaskStatus;

import java.sql.*;
import java.time.LocalDate;

public class JdbcCrudDemo {

    public static int insertTask(
            Connection conn,
            int userId,
            String title,
            Priority priority,
            TaskStatus status,
            LocalDate dueDate
    ) throws SQLException {

        // 下一步编写 INSERT SQL
        String sql = """
        INSERT INTO tasks (user_id, title, priority, status, due_date)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, priority.name());
            ps.setString(4, status.name());

            // 下一步处理 dueDate
            if (dueDate == null) {
                ps.setNull(5, Types.DATE);
            } else {
                ps.setDate(5, Date.valueOf(dueDate));
            }

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("新增任务失败，受影响行数：" + rows);
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("新增成功，但没有返回自增ID");
                }

                int generatedId = keys.getInt(1);

                return generatedId;
            }
        }
    }

    public static Task findById(Connection conn, int id) throws SQLException {

        String sql = """
        SELECT id, title, priority, status, due_date
        FROM tasks
        WHERE id = ?
        """;

        // 下一步编写查询代码
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return JdbcQueryDemo.mapRow(rs);
                }
            }
        }

        return null; // 暂时占位，后续替换
    }

    public static boolean updateStatus(
            Connection conn,
            int id,
            TaskStatus newStatus
    ) throws SQLException {

        String sql = """
        UPDATE tasks
        SET status = ?
        WHERE id = ?
        """;

         //下一步编写 PreparedStatement
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setInt(2, id);

            // 下一步执行 UPDATE
            int rows = ps.executeUpdate();

            return rows == 1;
        }
    }

    public static boolean deleteById(
            Connection conn,
            int id
    ) throws SQLException {

        String sql = """
        DELETE FROM tasks
        WHERE id = ?
        """;

        // 下一步编写 PreparedStatement
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            // 下一步执行 DELETE
            int rows = ps.executeUpdate();

            return rows == 1;
        }
    }

    public static void main(String[] args) throws SQLException {

        String url = System.getenv("TASKHUB_DB_URL");
        String username = System.getenv("TASKHUB_DB_USER");
        String password = System.getenv("TASKHUB_DB_PASSWORD");

        try (Connection conn = DriverManager.getConnection(
                url, username, password
        )) {
            System.out.println("数据库连接成功");

//            int newId = insertTask(
//                    conn,
//                    1,
//                    "Day39 JDBC新增测试",
//                    Priority.HIGH,
//                    TaskStatus.TODO,
//                    null
//            );
//
//            System.out.println("新增任务成功，ID：" + newId);

//            Task task = findById(conn, 5);
//
//            if (task != null) {
//                System.out.println("查询成功！");
//                System.out.println("任务ID：" + task.getId());
//                System.out.println("任务名称：" + task.getTitle());
//                System.out.println("任务状态：" + task.getStatus());
//            } else {
//                System.out.println("没有找到对应的任务");
//            }

            // 1. 修改前先查询任务
//            Task before = findById(conn, 5);
//            System.out.println("修改前状态：" + before.getStatus());
//
//            // 2. 修改任务状态
//            boolean success = updateStatus(conn, 5, TaskStatus.DONE);
//            System.out.println("修改是否成功：" + success);
//
//            // 3. 再次查询，检查修改结果
//            Task after = findById(conn, 5);
//            System.out.println("修改后状态：" + after.getStatus());

//            boolean success = updateStatus(
//                    conn,
//                    999,
//                    TaskStatus.DONE
//            );
//
//            System.out.println("修改是否成功：" + success);

//            boolean success = deleteById(conn, 999);
//
//            System.out.println("删除是否成功：" + success);

            // 1. 删除前查询
            Task before = findById(conn, 5);
            System.out.println("删除前任务是否存在：" + (before != null));

            // 2. 执行删除
            boolean success = deleteById(conn, 5);
            System.out.println("删除是否成功：" + success);

            // 3. 删除后再次查询
            Task after = findById(conn, 5);
            System.out.println("删除后任务是否存在：" + (after != null));

        }
    }
}
