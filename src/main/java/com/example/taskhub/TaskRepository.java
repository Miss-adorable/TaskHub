
package com.example.taskhub;

import java.util.List;

public interface TaskRepository {

    // 新增任务，成功返回保存后的 Task
    Task add(Task task);

    // 显示所有任务
    void showAll();

    // 根据 ID 删除
    boolean removeById(int id);

    // 根据关键词搜索
    boolean search(String keyword);

    // 根据 ID 查询
    Task findById(int id);

    // 查询所有任务
    List<Task> findAll();

    // 将任务修改保存到存储层
    boolean update(Task task);
}
