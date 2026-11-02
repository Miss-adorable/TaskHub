package com.example.taskhub;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {
    private final int id;
    private static int nextId = 1;
    private String title;
    private Priority priority;
    private TaskStatus status;
    private LocalDate dueDate;
    private Set<String> tags = new HashSet<>();

    public Task(String title, Priority priority, TaskStatus status) {
        this(nextId, title, priority, status);
        nextId++;
    }

    public Task(String title, Priority priority, TaskStatus status, LocalDate dueDate) {
        this(title, priority, status);
        this.dueDate = dueDate;
    }

    private Task(int id, String title, Priority priority, TaskStatus status) {

        if (id <= 0) {
            throw new IllegalArgumentException("任务ID必须大于0");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }

        if (priority == null) {
            throw new IllegalArgumentException("优先级不能为空");
        }

        if (status == null) {
            throw new IllegalArgumentException("任务状态不能为空");
        }

        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
    }

    @JsonCreator
    public static Task restore(
            @JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("status") TaskStatus status,
            @JsonProperty("tags") Set<String> tags,
            @JsonProperty("dueDate") LocalDate dueDate
    ) {
        // 保留你之前写好的方法内部代码
        Task task = new Task(id, title, priority, status);
        task.dueDate = dueDate;

        if (tags != null) {
            for (String tag : tags) {
                if (!task.addTag(tag)) {
                    throw new IllegalArgumentException("无效的任务标签");
                }
            }
        }

        nextId = Math.max(nextId, id + 1);

        return task;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public Set<String> getTags() {
        return new HashSet<>(tags);
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setTitle(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }

        this.title = title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("任务状态不能为空");
        }

        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void display() {
        System.out.println("任务ID：" + id);
        System.out.println("任务名称：" + title);
        System.out.println("优先级：" + priority);
        switch (status) {
            case TODO:
                System.out.println("任务状态：未开始");
                break;

            case IN_PROGRESS:
                System.out.println("任务状态：进行中");
                break;

            case DONE:
                System.out.println("任务状态：已完成");
                break;
        }
        System.out.println("任务标签：");
        showTags();
    }

    public boolean addTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return false;
        }

        return tags.add(tag.trim());
    }

    public boolean removeTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return false;
        }

        return tags.remove(tag.trim());
    }

    public boolean hasTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return false;
        }

        return tags.contains(tag.trim());
    }

    public void showTags() {
        if (tags.isEmpty()) {
            System.out.println("暂无标签");
            return;
        }

        for (String tag : tags) {
            System.out.println(tag);
        }
    }
}