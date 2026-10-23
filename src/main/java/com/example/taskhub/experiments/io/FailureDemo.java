package com.example.taskhub.experiments.io;

import com.example.taskhub.JsonTaskFileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.example.taskhub.Task;

import java.nio.file.StandardCopyOption;

public class FailureDemo {

    public static void main(String[] args) throws IOException {

        // 1. 使用独立的测试文件
        Path path = Path.of("data", "failure-test.json");

        Path backupPath = path.resolveSibling(
                "failure-test.json.bak"
        );

        Files.createDirectories(
                path.toAbsolutePath().getParent()
        );

        // 2. 模拟一份完好的旧备份
        Files.writeString(
                backupPath,
                "[{\"id\":1,\"title\":\"备份任务\",\"priority\":\"HIGH\",\"status\":\"TODO\",\"tags\":[]}]",
                StandardCharsets.UTF_8
        );

        // 3. 故意把正式文件写成错误的 JSON
        Files.writeString(
                path,
                "[{\"id\":1,",
                StandardCharsets.UTF_8
        );

        // 4. 尝试保存新的任务数据
        JsonTaskFileStore store = new JsonTaskFileStore(path);

        try {
            store.save(List.of());
            System.out.println("保存成功");
        } catch (IOException e) {
            System.out.println("保存失败：" + e.getMessage());
        }
        Path tempPath = path.resolveSibling("failure-test.json.tmp");

        System.out.println(
                "临时文件是否存在：" + Files.exists(tempPath)
        );

        // 5. 检查正式文件与备份文件
        System.out.println(
                "正式文件：" + Files.readString(path)
        );

        System.out.println(
                "备份文件：" + Files.readString(backupPath)
        );

        // 6. 从备份文件恢复任务
        JsonTaskFileStore backupStore = new JsonTaskFileStore(backupPath);

        List<Task> backupTasks = backupStore.load();

// 7. 输出恢复结果
        System.out.println("从备份恢复了 " + backupTasks.size() + " 个任务");

        for (Task task : backupTasks) {
            System.out.println("恢复的任务ID：" + task.getId());
            System.out.println("恢复的任务名称：" + task.getTitle());
        }

        // 1. 先保留损坏文件
        Path corruptPath = path.resolveSibling(
                "failure-test.json.corrupt"
        );

        Files.copy(
                path,
                corruptPath,
                StandardCopyOption.REPLACE_EXISTING
        );

// 2. 把有效备份复制到恢复用临时文件
        Path restoreTempPath = path.resolveSibling(
                "failure-test.json.restore.tmp"
        );

        Files.copy(
                backupPath,
                restoreTempPath,
                StandardCopyOption.REPLACE_EXISTING
        );

// 3. 使用原子移动替换正式文件
        Files.move(
                restoreTempPath,
                path,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println("正式文件恢复成功！");

        // 4. 重新读取恢复后的正式文件
        JsonTaskFileStore restoredStore = new JsonTaskFileStore(path);

        List<Task> restoredTasks = restoredStore.load();

// 5. 检查恢复结果
        System.out.println("恢复后的任务数量：" + restoredTasks.size());

        for (Task task : restoredTasks) {
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务名称：" + task.getTitle());
        }

// 6. 检查备份和临时文件
        System.out.println("备份文件是否存在：" + Files.exists(backupPath));

        System.out.println("恢复临时文件是否存在：" + Files.exists(restoreTempPath));
    }
}