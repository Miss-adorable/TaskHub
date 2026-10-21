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

public class JsonTaskFileStore {

    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonTaskFileStore(Path path) {
        this.path = path;
    }

    // 保存任务
    public void save(List<Task> tasks) throws IOException {

        String json;

        try {
            json = mapper.writeValueAsString(tasks);
        } catch (JacksonException e) {
            throw new IOException("任务序列化失败", e);
        }

        Files.createDirectories(path.getParent());

        Files.writeString(
                path,
                json,
                StandardCharsets.UTF_8
        );
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