package com.example.taskhub;

public class Task {
    private final int id;
    private static int nextId = 1;
    private String title;
    private Priority priority;
    private TaskStatus status;

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
    }
}