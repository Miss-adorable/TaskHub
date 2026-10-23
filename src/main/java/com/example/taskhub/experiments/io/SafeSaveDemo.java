package com.example.taskhub.experiments.io;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import tools.jackson.databind.ObjectMapper;

import java.nio.file.StandardCopyOption;

public class SafeSaveDemo {

    public static void main(String[] args) throws IOException {

        // 1. 指定实验用的正式文件
        Path path = Path.of("data", "safe-save-demo.json");

        // 2. 指定临时文件
        Path tempPath = path.resolveSibling(
                "safe-save-demo.json.tmp"
        );

        // 3. 创建目录
        Files.createDirectories(path.getParent());

        // 4. 模拟原来已经保存的数据
        Files.writeString(
                path,
                "[{\"title\":\"旧任务\"}]",
                StandardCharsets.UTF_8
        );

        // 5. 把新数据写入临时文件
        Files.writeString(
                tempPath,
                "[{\"title\":\"新任务\"}]",
                StandardCharsets.UTF_8
        );

        // 读取临时文件
        String tempJson = Files.readString(
                tempPath,
                StandardCharsets.UTF_8
        );

// 创建 Jackson 对象
        ObjectMapper mapper = new ObjectMapper();

// 检查 JSON 是否能够正常解析
        mapper.readTree(tempJson);

        System.out.println("临时文件 JSON 语法验证通过！");

        // 6. 分别读取两个文件
        System.out.println("正式文件：" + Files.readString(path));
        System.out.println("临时文件：" + Files.readString(tempPath));

        // 1. 指定备份文件路径
        Path backupPath = path.resolveSibling(
                "safe-save-demo.json.bak"
        );

// 2. 将原来的正式文件复制为备份文件
        Files.copy(
                path,
                backupPath,
                StandardCopyOption.REPLACE_EXISTING
        );

// 3. 查看备份内容
        System.out.println(
                "备份文件：" + Files.readString(backupPath)
        );

        try {
            Files.move(
                    tempPath,
                    path,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println("文件替换成功！");

        } catch (AtomicMoveNotSupportedException e) {
            System.out.println("当前文件系统不支持原子移动");
        }

        System.out.println("正式文件：" + Files.readString(path));
        System.out.println("临时文件是否存在：" + Files.exists(tempPath));
        System.out.println("备份文件：" + Files.readString(backupPath));
    }
}