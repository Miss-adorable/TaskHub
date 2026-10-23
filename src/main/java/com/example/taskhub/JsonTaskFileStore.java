package com.example.taskhub;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;

public class JsonTaskFileStore {

    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonTaskFileStore(Path path) {
        this.path = path;
    }

    public void save(List<Task> tasks) throws IOException {

        // 1. 将任务序列化为 JSON
        String json;

        try {
            json = mapper.writeValueAsString(tasks);
        } catch (JacksonException e) {
            throw new IOException("任务序列化失败", e);
        }

        // 2. 指定临时文件和备份文件
        String fileName = path.getFileName().toString();

        Path tempPath = path.resolveSibling(fileName + ".tmp");
        Path backupPath = path.resolveSibling(fileName + ".bak");

        // 3. 确保目录存在
        Files.createDirectories(path.toAbsolutePath().getParent());

        try {
            // 4. 写入临时文件
            Files.writeString(
                    tempPath,
                    json,
                    StandardCharsets.UTF_8
            );

            // 5. 验证临时文件
            try {
                String tempJson = Files.readString(
                        tempPath,
                        StandardCharsets.UTF_8
                );

                mapper.readTree(tempJson);

            } catch (JacksonException e) {
                throw new IOException("临时文件 JSON 验证失败", e);
            }

            // 6. 检查并备份原来的正式文件
            if (Files.exists(path)) {

                String oldJson = Files.readString(
                        path,
                        StandardCharsets.UTF_8
                );

                try {
                    mapper.readTree(oldJson);
                } catch (JacksonException e) {
                    throw new IOException("旧文件 JSON 已损坏，取消保存", e);
                }

                Files.copy(
                        path,
                        backupPath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            // 7. 用临时文件替换正式文件
            try {
                Files.move(
                        tempPath,
                        path,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );

            } catch (AtomicMoveNotSupportedException e) {
                throw new IOException("不支持原子移动，取消保存", e);
            }

        } finally {

            // 8. 清理可能残留的临时文件
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException e) {
                System.err.println("临时文件清理失败：" + e.getMessage());
            }
        }
    }
    // 加载任务
    public List<Task> load() throws IOException {

        if (Files.notExists(path)) {
            return new ArrayList<>();
        }

        String json = Files.readString(
                path,
                StandardCharsets.UTF_8
        );

        try {
            List<Task> tasks = mapper.readValue(
                    json,
                    new TypeReference<List<Task>>() {}
            );

            if (tasks == null) {
                throw new IOException("任务文件不能为 null");
            }

            return tasks;

        } catch (JacksonException e) {
            throw new IOException("JSON 解析失败", e);
        }
    }
}