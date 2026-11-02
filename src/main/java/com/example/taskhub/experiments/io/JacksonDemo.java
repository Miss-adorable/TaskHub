package com.example.taskhub.experiments.io;

import java.util.List;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Set;

import com.example.taskhub.Task;
import com.example.taskhub.Priority;
import com.example.taskhub.TaskStatus;

import tools.jackson.core.JacksonException;

public class JacksonDemo {
    public static void main(String[] args) {

        List<String> tasks = List.of("学习Java", "阅读论文");


        ObjectMapper mapper = new ObjectMapper();

        Task restored = Task.restore(
                8,
                "阅读论文",
                Priority.HIGH,
                TaskStatus.TODO,
                Set.of("科研"),
                null
        );

        System.out.println("恢复任务ID：" + restored.getId());
        System.out.println("恢复任务名称：" + restored.getTitle());

        Task newTask = new Task(
                "学习Python",
                Priority.MEDIUM,
                TaskStatus.TODO
        );

        System.out.println("新任务ID：" + newTask.getId());

        String json = mapper.writeValueAsString(tasks);

        Path path = Path.of("data", "jackson-demo.json");

        try {
            Files.createDirectories(path.getParent());

            Files.writeString(
                    path,
                    json,
                    StandardCharsets.UTF_8
            );

            System.out.println("JSON 保存成功！");

            // 第一步：读取 JSON 文件
            String fileJson = Files.readString(
                    path,
                    StandardCharsets.UTF_8
            );

// 第二步：反序列化
            List<String> restoredTasks = mapper.readValue(
                    fileJson,
                    new TypeReference<List<String>>() {}
            );

// 第三步：输出恢复后的集合
            System.out.println(restoredTasks.get(0));
            System.out.println(restoredTasks.get(1));

        } catch (IOException e) {
            System.out.println("文件保存失败：" + e.getMessage());
        }

        System.out.println(json);


        Task task = new Task(
                "学习Java",
                Priority.HIGH,
                TaskStatus.TODO
        );

        task.addTag("Java");
        task.addTag("学习");

        String taskJson = mapper.writeValueAsString(task);

        System.out.println(taskJson);

        // 1. 将两个 Task 对象放进集合
        List<Task> taskList = List.of(restored, newTask);

// 2. 把 Task 集合序列化成 JSON 字符串
        String jsonTasks = mapper.writeValueAsString(taskList);

        System.out.println(jsonTasks);

// 3. 指定 JSON 文件的位置
        Path taskPath = Path.of("data", "tasks-demo.json");

        try {
            // 4. 确保 data 文件夹存在
            Files.createDirectories(taskPath.getParent());

            // 5. 把 JSON 写入文件
            Files.writeString(
                    taskPath,
                    jsonTasks,
                    StandardCharsets.UTF_8
            );

            System.out.println("任务集合保存成功！");

            // 1. 从 JSON 文件读取内容
            String savedJson = Files.readString(
                    taskPath,
                    StandardCharsets.UTF_8
            );

// 2. 把 JSON 反序列化为 List<Task>
            List<Task> loadedTasks = mapper.readValue(
                    savedJson,
                    new TypeReference<List<Task>>() {}
            );

// 3. 检查恢复的任务
            for (Task t : loadedTasks) {
                System.out.println("任务ID：" + t.getId());
                System.out.println("任务名称：" + t.getTitle());
                System.out.println("优先级：" + t.getPriority());
                System.out.println("任务状态：" + t.getStatus());
                System.out.println("任务标签：" + t.getTags());
            }

        } catch (IOException e) {
            System.out.println("任务集合保存失败：" + e.getMessage());
        }catch (JacksonException e) {
            System.out.println("JSON转换失败：" + e.getMessage());
        }
    }
}