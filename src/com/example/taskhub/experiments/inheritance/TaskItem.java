package com.example.taskhub.experiments.inheritance;

public class TaskItem extends Item {

    private String status;

    public TaskItem(int id, String title, String status) {
        super(id, title);
        this.status = status;
    }

    @Override
    public void display() {
        super.display();
        System.out.println("状态：" + status);
    }
}