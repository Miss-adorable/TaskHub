package com.example.taskhub;
import java.util.Set;
import java.util.HashSet;

public class Task {
    private final int id;
    private static int nextId = 1;
    private String title;
    private Priority priority;
    private TaskStatus status;
    private Set<String> tags = new HashSet<>();

    public Task(String title, Priority priority, TaskStatus status) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }

        if (priority == null) {
            throw new IllegalArgumentException("优先级不能为空");
        }

        if (status == null) {
            throw new IllegalArgumentException("任务状态不能为空");
        }

        this.title = title;
        this.priority = priority;
        this.status = status;
        this.id = nextId;
        nextId++;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
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