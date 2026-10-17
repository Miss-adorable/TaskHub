package com.example.taskhub;

public class TaskService {

    private final TaskRepository repo;    //每个 TaskService 对象都保存着一个 TaskRepository 类型的引用。

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public boolean addTask(String title, Priority priority) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }

        Task task = new Task(title, priority, TaskStatus.TODO);

        return repo.add(task);
    }

    public void showAllTasks() {
        repo.showAll();
    }

    public boolean deleteTask(int id) {

        if (id <= 0) {
            return false;
        }

        return repo.removeById(id);
    }

    public boolean searchTasks(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        return repo.search(keyword);
    }

    public boolean markDone(int id) {

        Task task = repo.findById(id);

        if (task == null) {
            return false;
        }

        if (task.getStatus() == TaskStatus.DONE) {
            return false;
        }

        task.setStatus(TaskStatus.DONE);

        return true;
    }

    public boolean addTagToTask(int id, String tag) {
        Task task = repo.findById(id);

        if (task == null) {
            return false;
        }

        return task.addTag(tag);
    }

    public boolean removeTagFromTask(int id, String tag) {
        Task task = repo.findById(id);

        if (task == null) {
            return false;
        }

        return task.removeTag(tag);
    }

    public boolean hasTagInTask(int id, String tag) {
        Task task = repo.findById(id);

        if (task == null) {
            return false;
        }

        return task.hasTag(tag);
    }

}