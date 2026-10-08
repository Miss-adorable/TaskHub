package com.example.taskhub;

import java.util.Scanner;

public class Main {
    static void main(String[] args) {

        Task[] tasks =new Task[10];
        int taskCount = 0;

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
                    taskCount = addTask(tasks,taskCount,scanner);
                    break;

                case 2:
                    showTask(tasks,taskCount);
                    break;

                case 3:
                    taskCount = deleteTask(tasks,taskCount,scanner);
                    break;

                case 4:
                    searchTask(tasks,taskCount,scanner);
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

    static int addTask(Task[] tasks, int taskCount, Scanner scanner) {

        if (taskCount >= tasks.length) {
            System.out.println("任务已满，不能继续添加");
            return taskCount;
        }

        System.out.println("请输入任务名称：");
        String title = scanner.nextLine();

        if (title.isBlank()) {
            System.out.println("任务名称不能为空");
            return taskCount;
        }

        Task task = new Task();

        task.title = title;
        task.priority = 1;
        task.status = "未开始";

        tasks[taskCount] = task;
        taskCount++;

        System.out.println("添加成功");
        return taskCount;
    }

    static void showTask(Task[] tasks,int taskCount){
        if (taskCount == 0){
            System.out.println("暂无任务");
            return;
        }

        for(int i=0; i<taskCount; i++){
            System.out.println("任务 " + (i + 1) + "：");
            tasks[i].display();
            System.out.println();
        }
    }

    static int deleteTask(Task[] tasks, int taskCount, Scanner scanner){
        if(taskCount==0){
            System.out.println("暂无任务可删除");
            return taskCount;
        }

        System.out.println("请输入要删除的任务序号：");
        int number = scanner.nextInt();
        scanner.nextLine();

        int index = number-1;
        if(index < 0 || index >= taskCount){
            System.out.println("任务序号不存在");
            return taskCount;
        }

        for (int i=index;i<taskCount-1;i++){
            tasks[i]=tasks[i+1];
        }

        tasks[taskCount-1] = null;
        taskCount--;
        System.out.println("删除成功");
        return taskCount;
    }

    static void searchTask(Task[] tasks,int taskCount,Scanner scanner){
        System.out.println("请输入搜索关键词：");
        String keyWord = scanner.nextLine();

        boolean found = false;

        for (int i=0;i<taskCount;i++){
            if(tasks[i].title.contains(keyWord)){
                System.out.println("任务 " + (i + 1) + "：");
                tasks[i].display();
                found = true;
            }
        }

        if(!found){
            System.out.println("没有找到相关任务");
        }
    }



}

