package com.example.taskhub;

public interface TaskRepository {

    boolean add(Task task);

    void showAll();

    boolean delete(int number);

    boolean search(String keyword);
}