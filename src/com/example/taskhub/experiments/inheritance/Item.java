package com.example.taskhub.experiments.inheritance;

public class Item {

    private int id;
    private String title;

    public Item(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public void display() {
        System.out.println("ID：" + id);
        System.out.println("名称：" + title);
    }
}