package com.example.taskhub.experiments.io;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskFileDemo {
    public static void main(String[] args) {

        Path path = Path.of("data", "tasks.txt");

        try {
            // 第一步：确保 data 文件夹存在
            Files.createDirectories(path.getParent());

            // 第二步：如果文件不存在，就创建空文件
            if (Files.notExists(path)) {
                Files.createFile(path);
            }

            // 第三步：读取文件
            String content = Files.readString(
                    path,
                    StandardCharsets.UTF_8
            );

            // 第四步：判断文件是否为空
            if (content.isEmpty()) {
                System.out.println("当前没有保存任何任务");
            } else {
                System.out.println("已保存的任务：");
                System.out.println(content);
            }

        } catch (IOException e) {
            System.out.println("文件操作失败：" + e.getMessage());
        }
    }
}