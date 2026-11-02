package com.example.taskhub;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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


    @Test
    void findOverdueTasks_success() {

        // Arrange：准备测试环境
        TaskRepository repo = new InMemoryTaskRepository();
        TaskService service = new TaskService(repo);

        LocalDate today = LocalDate.of(2026, 11, 2);

        // 准备不同情况的任务
        repo.add(new Task("任务A", Priority.HIGH, TaskStatus.TODO,
                LocalDate.of(2026, 11, 1)));

        repo.add(new Task("任务B", Priority.HIGH, TaskStatus.TODO,
                LocalDate.of(2026, 11, 5)));

        repo.add(new Task("任务C", Priority.HIGH, TaskStatus.DONE,
                LocalDate.of(2026, 10, 30)));

        repo.add(new Task("任务D", Priority.HIGH, TaskStatus.TODO));

        repo.add(new Task("任务E", Priority.HIGH, TaskStatus.TODO,
                LocalDate.of(2026, 11, 2)));

        // Act：筛选逾期任务
        List<Task> result = service.findOverdueTasks(today);

        // Assert：检查筛选结果
        assertEquals(1, result.size());
        assertEquals("任务A", result.get(0).getTitle());
    }

}