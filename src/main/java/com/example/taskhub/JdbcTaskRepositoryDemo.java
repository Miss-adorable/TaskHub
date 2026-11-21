
package com.example.taskhub;

public class JdbcTaskRepositoryDemo {

    public static void main(String[] args) {

        // 1. 创建 JDBC Repository
        TaskRepository repo = new JdbcTaskRepository(1);

        // 2. 将 Repository 注入 TaskService
        TaskService service = new TaskService(repo);

        // 3. 删除任务9中的 SpringBoot 标签
        boolean result = service.removeTagFromTask(9, "SpringBoot");

        System.out.println("删除标签是否成功：" + result);

        // 4. 重新从 MySQL 查询任务9
        Task after = repo.findById(9);

        System.out.println("数据库中的任务标签：" + after.getTags());
        System.out.println("数据库中的任务状态：" + after.getStatus());
    }
}
