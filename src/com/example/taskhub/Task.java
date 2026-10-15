package com.example.taskhub;

public class Task {
    private final int id;
    private static int nextId = 1;
    private String title;
    private int priority;
    private String status;

    public Task(String title, int priority, String status) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }

        if (priority < 1 || priority > 3) {
            throw new IllegalArgumentException("优先级必须在1到3之间");
        }

        if (!"未开始".equals(status)
                && !"进行中".equals(status)
                && !"已完成".equals(status)) {
            throw new IllegalArgumentException("任务状态不合法");
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void display() {
        System.out.println("任务ID：" + id);
        System.out.println("任务名称：" + title);
        System.out.println("优先级：" + priority);
        System.out.println("任务状态：" + status);
    }
}