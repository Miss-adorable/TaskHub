package com.example.taskhub;

public class InMemoryTaskRepository implements TaskRepository {

    private Task[] tasks = new Task[10];
    private int taskCount = 0;

    @Override
    public boolean add(Task task) {

        if (taskCount >= tasks.length) {
            return false;
        }

        tasks[taskCount] = task;
        taskCount++;

        return true;
    }

    @Override
    public void showAll() {

        if (taskCount == 0) {
            System.out.println("暂无任务");
            return;
        }

        for (int i = 0; i < taskCount; i++) {
            tasks[i].display();
        }
    }

    @Override
    public boolean delete(int number) {

        int index = number - 1;

        if (index < 0 || index >= taskCount) {
            return false;
        }

        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }

        tasks[taskCount - 1] = null;
        taskCount--;

        return true;
    }

    @Override
    public boolean search(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return false;
        }

        boolean found = false;

        for (int i = 0; i < taskCount; i++) {

            if (tasks[i].getTitle().contains(keyword)) {

                System.out.println("任务 " + (i + 1) + "：");
                tasks[i].display();

                found = true;
            }
        }

        return found;
    }

    @Override
    public Task findById(int id) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getId() == id) {
                return tasks[i];
            }
        }
        return null;
    }
}