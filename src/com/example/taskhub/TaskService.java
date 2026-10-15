package com.example.taskhub;

public class TaskService {

    private final TaskRepository repo;    //每个 TaskService 对象都保存着一个 TaskRepository 类型的引用。

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public boolean addTask(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }

        Task task = new Task(title, 1, "未开始");

        return repo.add(task);
    }

    public void showAllTasks() {
        repo.showAll();
    }

    public boolean deleteTask(int number) {

        if (number <= 0) {
            return false;
        }

        return repo.delete(number);
    }

    public boolean searchTasks(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        return repo.search(keyword);
    }
}