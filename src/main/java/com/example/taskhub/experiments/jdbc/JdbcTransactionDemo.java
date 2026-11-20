package com.example.taskhub.experiments.jdbc;

import java.sql.*;

public class JdbcTransactionDemo {

    public static int registerUserWithWelcomeTask(
            Connection conn,
            String username,
            String displayName
    ) throws SQLException {

        boolean originalAutoCommit = conn.getAutoCommit();

        if (!originalAutoCommit) {
            throw new SQLException("连接已经处于手动事务模式");
        }
        conn.setAutoCommit(false);

        boolean transactionCompleted = false;
        Exception originalError = null;

        try {
            int userId = insertUser(conn, username, displayName);

            insertWelcomeTask(conn, userId);

            conn.commit();
            transactionCompleted = true;

            return userId;

        } catch (SQLException | RuntimeException e) {
            // 后续在这里处理 rollback
            originalError = e;
            try {
                conn.rollback();
                transactionCompleted = true;
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }
            throw e;

        } finally {
            if (transactionCompleted) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException restoreError) {
                    if (originalError != null) {
                        originalError.addSuppressed(restoreError);
                    } else {
                        throw restoreError;
                    }
                }
            }
        }

    }

    private static int insertUser(
            Connection conn,
            String username,
            String displayName
    ) throws SQLException {

        String sql = """
        INSERT INTO users (username, display_name)
        VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, username);
            ps.setString(2, displayName);

            // 下一步执行 INSERT 并获取自增 ID
            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("新增用户失败，受影响行数：" + rows);
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("新增用户成功，但没有获取到自增ID");
                }

                return keys.getInt(1);
            }
        }
    }

    private static void insertWelcomeTask(
            Connection conn,
            int userId
    ) throws SQLException {

        String sql = """
        INSERT INTO tasks (user_id, title, priority, status)
        VALUES (?, '欢迎使用 TaskHub', 'LOW', 'TODO')
        """;

        // 下一步添加 PreparedStatement
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("创建欢迎任务失败，受影响行数：" + rows);
            }
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

//            int userId = registerUserWithWelcomeTask(
//                    conn,
//                    "java_day40_rollback_01",
//                    "Day40回滚测试用户"
//            );

            System.out.println("注册成功，用户ID：" + userId);
            System.out.println("autoCommit状态：" + conn.getAutoCommit());
        }
    }
}