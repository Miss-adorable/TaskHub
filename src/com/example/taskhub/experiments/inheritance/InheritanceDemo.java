package com.example.taskhub.experiments.inheritance;

public class InheritanceDemo {

    public static void main(String[] args) {

        Item item = new Item(1, "普通条目");

        TaskItem task = new TaskItem(2, "学习Java", "未开始");

        System.out.println("=== 父类对象 ===");
        item.display();

        System.out.println("=== 子类对象 ===");
        task.display();
    }
}