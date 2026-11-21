package com.example.taskhub;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskRepository implements TaskRepository {

    // 使用 HashMap 在内存中保存任务
    // key：任务 ID（Integer）
    // value：任务对象（Task）
    private Map<Integer, Task> taskById = new HashMap<>();

    /**
     * 1. add()：添加任务
     * 作用：将一个 Task 对象保存到 HashMap 中。
     * 执行过程：
     * ① 判断任务对象是否为 null。
     * ② 获取任务的 ID。
     * ③ 检查该 ID 是否已经存在，避免重复添加。
     * ④ 如果不存在，则将任务保存到 HashMap。
     * 返回值：
     * - 添加成功：返回保存的 Task 对象。
     * - 添加失败：返回 null。
     */
    @Override
    public Task add(Task task) {

        if (task == null) {
            return null;
        }

        int id = task.getId();

        if (taskById.containsKey(id)) {
            return null;
        }

        taskById.put(id, task);

        return task;
    }

    /**
     * 2. showAll()：显示所有任务
     * 作用：遍历 HashMap 中保存的全部任务，并输出任务信息。
     * 执行过程：
     * ① 判断 HashMap 是否为空。
     * ② 如果为空，输出“暂无任务”，结束方法。
     * ③ 如果不为空，遍历所有 Task 对象。
     * ④ 调用每个 Task 对象的 display() 方法显示信息。
     * 返回值：void，不返回任何数据。
     */
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

    /**
     * 3. removeById()：根据 ID 删除任务
     * 作用：从 HashMap 中删除指定 ID 对应的任务。
     * 执行过程：
     * ① 调用 remove(id)，根据 ID 删除任务。
     * ② remove() 返回被删除的 Task 对象。
     * ③ 如果 ID 不存在，remove() 返回 null。
     * 返回值：
     * - true：任务存在，删除成功。
     * - false：任务不存在，删除失败。
     */
    @Override
    public boolean removeById(int id) {

        Task removed = taskById.remove(id);

        return removed != null;
    }

    /**
     * 4. search()：根据关键词搜索任务
     * 作用：查找任务名称中包含指定关键词的所有任务。
     * 执行过程：
     * ① 检查关键词是否为 null 或空白字符串。
     * ② 遍历 HashMap 中的所有任务。
     * ③ 使用 contains(keyword) 判断任务名称是否包含关键词。
     * ④ 如果匹配，输出任务 ID 和任务详细信息。
     * ⑤ 使用 found 记录是否找到匹配的任务。
     * 返回值：
     * - true：至少找到一条匹配任务。
     * - false：没有找到匹配任务，或关键词无效。
     */
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

    /**
     * 5. findById()：根据 ID 查询单个任务
     * 作用：从 HashMap 中获取指定 ID 对应的 Task 对象。
     * 执行过程：
     * ① 调用 taskById.get(id)。
     * ② HashMap 根据 key（任务 ID）查找对应的 value（Task 对象）。
     * 返回值：
     * - 找到任务：返回对应的 Task 对象。
     * - 没有找到：返回 null。
     * 注意：此方法只查询任务，不修改任务数据。
     */
    @Override
    public Task findById(int id) {
        return taskById.get(id);
    }

    /**
     * 6. findAll()：查询所有任务
     * 作用：获取 HashMap 中保存的全部 Task 对象。
     * 执行过程：
     * ① taskById.values() 获取所有 Task 对象组成的集合。
     * ② new ArrayList<>() 将这些对象放入一个新的 List。
     * ③ 返回这个 List。
     * 返回值：
     * - 返回 List<Task>，里面包含当前所有任务。
     * - 如果没有任务，返回空 List，而不是 null。
     * 注意：
     * 新建的是 List 集合，里面的 Task 仍然是原来的对象引用，
     * 并没有复制出新的 Task 对象。
     */
    @Override
    public List<Task> findAll() {
        return new ArrayList<>(taskById.values());
    }

    /**
     * 7. update()：更新已有任务
     * 作用：根据任务 ID，将 HashMap 中原来的任务替换为传入的 Task 对象。
     * 执行过程：
     * ① 检查传入的 Task 是否为 null。
     * ② 获取需要更新的任务 ID。
     * ③ 使用 containsKey(id) 检查该任务是否存在。
     * ④ 如果不存在，返回 false，不进行更新。
     * ⑤ 如果存在，使用 put(id, task) 替换原来的任务对象。
     * 返回值：
     * - true：任务存在，已执行替换操作。
     * - false：任务为 null，或者指定 ID 不存在。
     * 注意：
     * HashMap 的 put() 方法在 key 已存在时会覆盖原来的 value。
     * 因此这里的 update() 只允许更新已有任务，不允许创建新任务。
     * 更新仅发生在当前内存仓库中，不会自动更新 MySQL 数据库。
     */
    @Override
    public boolean update(Task task) {

        if (task == null) {
            return false;
        }

        int id = task.getId();

        if (!taskById.containsKey(id)) {
            return false;
        }

        taskById.put(id, task);

        return true;
    }
}