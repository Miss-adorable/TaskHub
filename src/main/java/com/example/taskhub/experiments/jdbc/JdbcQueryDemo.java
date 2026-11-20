package com.example.taskhub.experiments.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.example.taskhub.Task;
import com.example.taskhub.Priority;
import com.example.taskhub.TaskStatus;

import java.sql.Date;
import java.time.LocalDate;

public class JdbcQueryDemo {

    static Task mapRow(ResultSet rs) throws SQLException {

        // 1. 读取基本字段
        int id = rs.getInt("id");
        String title = rs.getString("title");

        // 2. 字符串转换为枚举
        Priority priority = Priority.valueOf(
                rs.getString("priority")
        );

        TaskStatus status = TaskStatus.valueOf(
                rs.getString("status")
        );

        // 3. 处理日期
        Date date = rs.getDate("due_date");
        LocalDate dueDate = null;

        if (date != null) {
            dueDate = date.toLocalDate();
        }

        // 4. 恢复 Task 对象
        return Task.restore(
                id, title, priority, status, null, dueDate
        );
    }

    public static void main(String[] args) throws SQLException {

        String url = System.getenv("TASKHUB_DB_URL");
        String username = System.getenv("TASKHUB_DB_USER");
        String password = System.getenv("TASKHUB_DB_PASSWORD");

        String sql = """
        SELECT id, title, priority, status, due_date
        FROM tasks
        ORDER BY id
        LIMIT ?
        """;

        // TODO：在这里编写数据库连接代码
        try (
                Connection conn = DriverManager.getConnection(
                        url, username, password
                );
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, 10);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("SQL 查询执行成功");

                while (rs.next()) {
                    Task task = mapRow(rs);

                    System.out.println(
                            "任务ID：" + task.getId()
                                    + "，任务名称：" + task.getTitle()
                                    + "，任务状态：" + task.getStatus()
                    );
                }
            }
        }

    }
}