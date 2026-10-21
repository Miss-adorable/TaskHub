package com.example.taskhub;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskRepository implements TaskRepository {

    private Map<Integer, Task> taskById = new HashMap<>();

    @Override
    public boolean add(Task task) {

        if (task == null) {
            return false;
        }

        int id = task.getId();

        if (taskById.containsKey(id)) {
            return false;
        }

        taskById.put(id, task);

        return true;
    }

    @Override
    public void showAll() {

        if (taskById.isEmpty()) {
            System.out.println("暂无任务");
            return;
        }

        for (Task task : taskById.values()) {
            task.display();
        }
    }

    @Override
    public boolean removeById(int id) {

        Task removed = taskById.remove(id);

        return removed != null;
    }

    @Override
    public boolean search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return false;
        }

        boolean found = false;

        for (Task current : taskById.values()) {

            if (current.getTitle().contains(keyword)) {

                System.out.println("任务ID：" + current.getId());
                current.display();

                found = true;
            }
        }

        return found;
    }

    @Override
    public Task findById(int id) {
        return taskById.get(id);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(taskById.values());
    }
}