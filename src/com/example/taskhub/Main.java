package com.example.taskhub;

import java.util.Scanner;

public class Main {



    public static void main(String[] args) {

        TaskRepository repo = new InMemoryTaskRepository();

        TaskService service = new TaskService(repo);

        Scanner scanner =new Scanner(System.in);



        while (true){
            showMenu();

            System.out.println("请输入你的选择：");

            if(!scanner.hasNextInt()){
                System.out.println("请输入数字！");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice){
                case 1:
                    addTask(service, scanner);
                    break;

                case 2:
                    showTask(service);
                    break;

                case 3:
                    deleteTask(service, scanner);
                    break;

                case 4:
                    searchTask(service, scanner);
                    break;

                case 5:
                    markTaskDone(service, scanner);
                    break;

                case 6:
                    addTag(service, scanner);
                    break;

                case 7:
                    removeTag(service, scanner);
                    break;

                case 8:
                    checkTaskTag(service, scanner);
                    break;

                case 0:
                    System.out.println("退出程序");
                    return;

                default:
                    System.out.println("请输入0-8！");
            }
        }
    }
    static void showMenu() {
        System.out.println("1.添加任务");
        System.out.println("2.查看任务");
        System.out.println("3.删除任务");
        System.out.println("4.搜索任务");
        System.out.println("5.标记任务为 DONE");
        System.out.println("6.添加任务标签");
        System.out.println("7.删除任务标签");
        System.out.println("8.查询任务标签");
        System.out.println("0.退出");
    }
    static void addTask(TaskService service, Scanner scanner) {

        System.out.println("请输入任务名称：");
        String title = scanner.nextLine();

        // ① 展示全部优先级
        System.out.println("请选择任务优先级：");

        for (Priority p : Priority.values()) {
            System.out.println(p);
        }

        // ② 接收用户输入
        System.out.println("请输入优先级：");
        String input = scanner.nextLine().trim().toUpperCase();

        // ③ 将 String 转换成 Priority
        Priority priority;

        try {
            priority = Priority.valueOf(input);
        } catch (IllegalArgumentException e) {
            System.out.println("优先级无效，请输入 HIGH、MEDIUM 或 LOW");
            return;
        }

        // ④ 添加任务
        try {
            boolean result = service.addTask(title, priority);

            if (result) {
                System.out.println("添加成功");
            } else {
                System.out.println("添加失败");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    static void showTask(TaskService service) {
        service.showAllTasks();
    }

    static void deleteTask(TaskService service, Scanner scanner) {

        System.out.println("请输入要删除的任务ID：");

        if (!scanner.hasNextInt()) {
            System.out.println("请输入正确的数字！");
            scanner.nextLine();
            return;
        }

        int id = scanner.nextInt();
        scanner.nextLine();

        boolean result = service.deleteTask(id);

        if (result) {
            System.out.println("删除成功");
        } else {
            System.out.println("任务ID不存在");
        }
    }

    static void searchTask(TaskService service, Scanner scanner) {

        System.out.println("请输入搜索关键词：");
        String keyword = scanner.nextLine();

        try {
            boolean found = service.searchTasks(keyword);

            if (!found) {
                System.out.println("没有找到匹配的任务");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("搜索失败：" + e.getMessage());
        }
    }

    static void markTaskDone(TaskService service, Scanner scanner) {

        System.out.println("请输入要标记完成的任务ID：");

        if (!scanner.hasNextInt()) {
            System.out.println("请输入正确的数字！");
            scanner.nextLine();
            return;
        }

        int id = scanner.nextInt();
        scanner.nextLine();

        boolean result = service.markDone(id);

        if (result) {
            System.out.println("修改状态成功！");
        } else {
            System.out.println("修改状态失败！");
        }
    }

    static void addTag(TaskService service, Scanner scanner) {

        System.out.println("请输入要添加标签的任务ID：");

        if (!scanner.hasNextInt()) {
            System.out.println("请输入正确的数字！");
            scanner.nextLine();
            return;
        }

        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("请输入标签名称：");
        String tag = scanner.nextLine();

        boolean result = service.addTagToTask(id, tag);

        if (result) {
            System.out.println("标签添加成功！");
        } else {
            System.out.println("标签添加失败：任务不存在、标签为空或标签已存在！");
        }
    }

    static void removeTag(TaskService service, Scanner scanner) {

        System.out.println("请输入要删除标签的任务ID：");

        if (!scanner.hasNextInt()) {
            System.out.println("请输入正确的数字！");
            scanner.nextLine();
            return;
        }

        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("请输入要删除的标签：");
        String tag = scanner.nextLine();

        boolean result = service.removeTagFromTask(id, tag);

        if (result) {
            System.out.println("标签删除成功！");
        } else {
            System.out.println("标签删除失败：任务不存在或标签不存在！");
        }
    }

    static void checkTaskTag(TaskService service, Scanner scanner) {

        System.out.println("请输入要查询的任务ID：");

        if (!scanner.hasNextInt()) {
            System.out.println("请输入正确的数字！");
            scanner.nextLine();
            return;
        }

        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("请输入要查询的标签：");
        String tag = scanner.nextLine();

        boolean result = service.hasTagInTask(id, tag);

        if (result) {
            System.out.println("该任务存在此标签！");
        } else {
            System.out.println("该任务不存在此标签，或任务ID不存在！");
        }
    }



}

