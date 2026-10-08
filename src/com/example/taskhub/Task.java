package com.example.taskhub;

public class Task {
    String title;
    int priority;
    String status;

    void display() {
        System.out.println("任务名称：" + title);
        System.out.println("优先级：" + priority);
        System.out.println("任务状态：" + status);
    }
}
