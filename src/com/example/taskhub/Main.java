package com.example.taskhub;

import java.util.Scanner;

public class Main {



    public static void main(String[] args) {

        TaskRepository repo = new InMemoryTaskRepository();

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
                    addTask(repo, scanner);
                    break;

                case 2:
                    showTask(repo);
                    break;

                case 3:
                    deleteTask(repo, scanner);
                    break;

                case 4:
                    searchTask(repo, scanner);
                    break;

                case 0:
                    System.out.println("退出程序");
                    return;

                default:
                    System.out.println("请输入0-4！");
            }
        }
    }
    static void showMenu(){
        System.out.println("1. 添加任务 [NEW]");
        System.out.println("2.查看任务");
        System.out.println("3.删除任务");
        System.out.println("4.搜索任务");
        System.out.println("0.退出");
    }

    static void addTask(TaskRepository repo, Scanner scanner) {

        System.out.println("请输入任务名称：");
        String title = scanner.nextLine();

        if (title.isBlank()) {
            System.out.println("任务名称不能为空");
            return;
        }

        try {
            Task task = new Task(title, 1, "未开始");

            boolean result = repo.add(task);

            if (result) {
                System.out.println("添加成功");
            } else {
                System.out.println("任务已满，添加失败");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    static void showTask(TaskRepository repo) {
        repo.showAll();
    }

    static void deleteTask(TaskRepository repo, Scanner scanner) {

        System.out.println("请输入要删除的任务序号：");

        if (!scanner.hasNextInt()) {
            System.out.println("请输入正确的数字！");
            scanner.nextLine();
            return;
        }

        int number = scanner.nextInt();
        scanner.nextLine();

        boolean result = repo.delete(number);

        if (result) {
            System.out.println("删除成功");
        } else {
            System.out.println("任务序号不存在");
        }
    }

    static void searchTask(TaskRepository repo, Scanner scanner) {

        System.out.println("请输入搜索关键词：");
        String keyword = scanner.nextLine();

        if (keyword.isBlank()) {
            System.out.println("搜索关键词不能为空");
            return;
        }

        boolean found = repo.search(keyword);

        if (!found) {
            System.out.println("没有找到相关任务");
        }
    }



}

