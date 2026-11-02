package com.example.taskhub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    @Test
    void addTask_success() throws Exception {

        // 1. Arrange：准备测试环境
        TaskRepository repo = new InMemoryTaskRepository();
        TaskService service = new TaskService(repo);

        // 2. Act：执行添加任务
        boolean result = service.addTask(
                "学习Java",
                Priority.HIGH
        );

        // 3. Assert：检查结果
        assertTrue(result);
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void addTask_emptyTitle() {

        // 1. Arrange：准备测试环境
        TaskRepository repo = new InMemoryTaskRepository();
        TaskService service = new TaskService(repo);

        // 2. Act + Assert：验证是否抛出预期异常
        assertThrows(
                InvalidTaskException.class,
                () -> {
                    service.addTask("", Priority.HIGH);
                }
        );

        // 3. Assert：确认没有添加任何任务
        assertEquals(0, repo.findAll().size());
    }

    @Test
    void deleteTask_notFound() {

        // Arrange：创建空仓库
        TaskRepository repo = new InMemoryTaskRepository();
        TaskService service = new TaskService(repo);

        // Act + Assert：删除不存在的任务
        assertThrows(
                TaskNotFoundException.class,
                () -> {
                    service.deleteTask(999);
                }
        );

        // Assert：确认仓库仍然为空
        assertEquals(0, repo.findAll().size());
    }

    @Test
    void markDone_success() {

        // 1. Arrange：准备测试数据
        TaskRepository repo = new InMemoryTaskRepository();
        TaskService service = new TaskService(repo);

        service.addTask("学习Java", Priority.HIGH);

        Task task = repo.findAll().get(0);

        // 检查任务初始状态
        assertEquals(TaskStatus.TODO, task.getStatus());

        // 2. Act：将任务标记为完成
        boolean result = service.markDone(task.getId());

        // 3. Assert：验证结果
        assertTrue(result);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }
}