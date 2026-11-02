package com.example.taskhub;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskJsonTest {

    @Test
    void restoreOldTask_withoutDueDate() throws Exception {

        // Arrange：模拟以前保存的 JSON
        String json = """
                {
                    "id": 1,
                    "title": "旧任务",
                    "priority": "HIGH",
                    "status": "TODO",
                    "tags": []
                }
                """;

        ObjectMapper mapper = new ObjectMapper();

        // Act：将 JSON 恢复为 Task 对象
        Task task = mapper.readValue(json, Task.class);

        // Assert：验证恢复结果
        assertEquals("旧任务", task.getTitle());
        assertNull(task.getDueDate());
    }

    @Test
    void restoreNewTask_withDueDate() throws Exception {

        // Arrange：创建带截止日期的任务
        LocalDate date = LocalDate.of(2026, 11, 5);

        Task task = new Task(
                "学习Java",
                Priority.HIGH,
                TaskStatus.TODO,
                date
        );

        ObjectMapper mapper = new ObjectMapper();

        // Act：序列化，再反序列化
        String json = mapper.writeValueAsString(task);

        Task restored = mapper.readValue(json, Task.class);

        // Assert：检查日期是否成功恢复
        assertEquals(date, restored.getDueDate());
    }
}