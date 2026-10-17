package com.example.taskhub;
import java.util.List;

public interface TaskRepository {

    boolean add(Task task);

    void showAll();

    boolean removeById(int id);

    boolean search(String keyword);

    Task findById(int id);

    List<Task> findAll();


}