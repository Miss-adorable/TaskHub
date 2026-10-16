package com.example.taskhub;

public interface TaskRepository {

    boolean add(Task task);

    void showAll();

    boolean removeById(int id);

    boolean search(String keyword);

    Task findById(int id);


}