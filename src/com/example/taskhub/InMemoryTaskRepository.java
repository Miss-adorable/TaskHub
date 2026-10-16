package com.example.taskhub;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskRepository implements TaskRepository {

    private List<Task> tasks = new ArrayList<>();

    @Override
    public boolean add(Task task) {
        return tasks.add(task);
    }

    @Override
    public void showAll() {

        if (tasks.isEmpty()) {
            System.out.println("暂无任务");
            return;
        }

        for (Task task : tasks) {
            task.display();
        }
    }

    @Override
    public boolean removeById(int id) {

        for (int i = 0; i < tasks.size(); i++) {

            Task current = tasks.get(i);

            if (current.getId() == id) {

                tasks.remove(i);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return false;
        }

        boolean found = false;

        for (int i = 0; i < tasks.size(); i++) {

            Task current = tasks.get(i);

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

        for (Task task : tasks) {

            if (task.getId() == id) {
                return task;
            }
        }

        return null;
    }
}