# Java Day 38：JDBC 连接与查询

## 1. JDBC 基础与核心对象

JDBC（Java Database Connectivity）是 Java 访问关系型数据库的一套标准 API。Java 使用 JDBC 接口，MySQL Connector/J 提供 MySQL 数据库的驱动实现。

本次实践环境：

- Java：JDK 25
- 数据库：MySQL 8.4
- 数据库驱动：MySQL Connector/J
- 实验类：`com.example.taskhub.experiments.jdbc.JdbcQueryDemo`
- 当前连接数据库：`taskhub`
- 查询表：`tasks`

本地 Maven 项目添加的驱动依赖：

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>26.10.0</version>
</dependency>
```

### JDBC 三个核心对象

| 对象 | 职责 |
| --- | --- |
| `Connection` | 建立和管理数据库连接 |
| `PreparedStatement` | 准备 SQL、绑定参数、执行 SQL |
| `ResultSet` | 表示查询结果，供 Java 逐行读取 |

**易混淆：PreparedStatement 负责执行查询，ResultSet 负责读取查询结果。**

```java
Connection conn = DriverManager.getConnection(url, username, password);
PreparedStatement ps = conn.prepareStatement(sql);
ResultSet rs = ps.executeQuery();
```

整个查询流程：**连接数据库 → 准备 SQL → 绑定参数 → 执行查询 → 遍历结果 → 映射对象。**

## 2. 数据库连接与环境变量

```java
String url = System.getenv("TASKHUB_DB_URL");
String username = System.getenv("TASKHUB_DB_USER");
String password = System.getenv("TASKHUB_DB_PASSWORD");

Connection conn = DriverManager.getConnection(url, username, password);
```

`DriverManager.getConnection()` 根据 JDBC URL、用户名和密码建立连接。

本次连接地址的格式：

```text
jdbc:mysql://localhost:3306/taskhub
```

- `jdbc:mysql`：MySQL JDBC 连接协议。
- `localhost`：本机。
- `3306`：MySQL 端口。
- `taskhub`：连接后默认使用的数据库。

### 数据库和同名数据表

本次查询的完整表名为 `taskhub.tasks`，控制台输出了 3 条任务。

Day 37 索引实验使用的是另一个数据库：`taskhub_day33_test.tasks`，当时有 100 条测试任务。

**两个数据库中都存在 `tasks` 表，不代表它们是同一张表。** 如果不指定数据库前缀，`FROM tasks` 默认针对当前连接的数据库。

### 为什么使用环境变量？

- 避免把数据库密码直接写到 Java 源代码中。
- 不同机器可以单独配置数据库地址和账号。
- 降低代码提交到 GitHub 时泄露凭据的风险。

注意：环境变量不是加密；不要上传密码、`.env` 配置文件或含密码的截图。

### 实际排错

第一次运行时出现：

```text
java.sql.SQLException: The url cannot be null
```

原因是 IntelliJ IDEA 对应的运行配置没有正确提供数据库连接环境变量。设置 `TASKHUB_DB_URL`、`TASKHUB_DB_USER`、`TASKHUB_DB_PASSWORD` 后连接成功。

## 3. PreparedStatement：准备与执行 SQL

本次实验 SQL：

```java
String sql = """
    SELECT id, title, priority, status, due_date
    FROM tasks
    ORDER BY id
    LIMIT ?
    """;
```

Java 的 `"""` 是文本块，适合保存多行 SQL。

| SQL 片段 | 作用 |
| --- | --- |
| `SELECT id, ...` | 指定查询字段 |
| `FROM tasks` | 指定数据表 |
| `ORDER BY id` | 按 ID 升序排列 |
| `LIMIT ?` | 最多返回指定条数 |

### 关键区别：prepareStatement() 和 executeQuery()

```java
PreparedStatement ps = conn.prepareStatement(sql);
ps.setInt(1, 10);
ResultSet rs = ps.executeQuery();
```

1. `prepareStatement(sql)`：创建 `PreparedStatement`，准备 SQL，**不是执行 SELECT 查询**。
2. `setInt(1, 10)`：为第一个 `?` 绑定整数 10。
3. `executeQuery()`：**真正执行 SELECT 查询**，返回 `ResultSet`。

`LIMIT 10` 表示最多返回 10 条；如果表里只有 3 条符合条件的记录，就返回 3 条。

### 参数绑定

```sql
SELECT *
FROM tasks
WHERE user_id = ?
AND status = ?;
```

```java
ps.setInt(1, 2);
ps.setString(2, "TODO");
```

- 第一个参数是占位符的位置，从 **1** 开始。
- 第二个参数是绑定的实际值。
- 参数顺序必须与 SQL 中的 `?` 顺序对应。

使用参数绑定，而不是拼接用户输入，有助于防止 SQL 注入。

## 4. ResultSet：游标与 next()（重点薄弱项）

执行 `ps.executeQuery()` 后得到 `ResultSet`。它提供了访问查询结果的接口。

假设结果有三行：

| id | title |
| --- | --- |
| 2 | 学习 MySQL |
| 3 | 阅读论文 |
| 4 | 完成Java作业 |

### 1. 初始位置

**刚获得 ResultSet 时，游标位于第一行之前，不是第一行。**

必须先调用 `rs.next()`，才能移动到第一条数据。

### 2. next() 做了两件事

`rs.next()`：

1. 尝试将游标移动到下一行。
2. 有下一行则返回 `true`，否则返回 `false`。

```java
while (rs.next()) {
    String title = rs.getString("title");
    System.out.println(title);
}
```

| next() 调用 | 游标位置 | 返回值 | 是否执行循环体 |
| --- | --- | --- | --- |
| 第 1 次 | id = 2 | true | 是 |
| 第 2 次 | id = 3 | true | 是 |
| 第 3 次 | id = 4 | true | 是 |
| 第 4 次 | 最后一行之后 | false | 否 |

**3 行数据：next() 调用 4 次，循环体执行 3 次。** 最后一次调用用于判断没有下一行。

### 3. next() 与 getString() 的区别

```java
rs.next();                         // 移动到下一行
String title = rs.getString("title");  // 读取当前行的 title
```

- `next()`：负责**移动游标**。
- `getString()`、`getInt()`、`getDate()`：负责**读取当前行的字段**，不会移动游标。

例如：

```java
rs.next();  // 第一行 id=2
rs.next();  // 第二行 id=3
String title = rs.getString("title");
```

得到的标题是 `"阅读论文"`。

### 4. 如果查询结果为空

第一次 `rs.next()` 就返回 `false`，`while` 循环体一次也不会执行。

**没有查询到数据，不等于 SQL 执行失败。**

## 5. 数据库字段与 Java 数据类型转换

`tasks` 数据表与 Task 类字段的对应关系：

| 数据库字段 | 数据库存储 | Java Task 使用 |
| --- | --- | --- |
| `id` | INT | `int` |
| `title` | VARCHAR | `String` |
| `priority` | VARCHAR | `Priority` 枚举 |
| `status` | VARCHAR | `TaskStatus` 枚举 |
| `due_date` | DATE，可为 NULL | `LocalDate` |

### 读取基本字段

```java
int id = rs.getInt("id");
String title = rs.getString("title");
```

这里的字符串 `"id"` 和 `"title"` 是 SQL 查询结果中的列名。

### 字符串转枚举

```java
Priority priority = Priority.valueOf(rs.getString("priority"));
TaskStatus status = TaskStatus.valueOf(rs.getString("status"));
```

数据库的 `"HIGH"` 是字符串，Task 的 `priority` 是枚举，二者不能直接赋值。

`valueOf()` 根据常量名称转换，并且**区分大小写**：

```java
Priority.valueOf("HIGH"); // 成功
Priority.valueOf("high"); // IllegalArgumentException
```

枚举值名称不匹配会抛出 `IllegalArgumentException`，不是 `SQLException`。

## 6. java.sql.Date、LocalDate 与 NULL（重点薄弱项）

Task 类定义：

```java
private LocalDate dueDate;
```

JDBC 读取 SQL DATE：

```java
Date date = rs.getDate("due_date");

LocalDate dueDate = null;
if (date != null) {
    dueDate = date.toLocalDate();
}
```

此处的 `Date` 是 `java.sql.Date`，不是 `java.util.Date`。

### 为什么先判断 null？

如果数据库中 `due_date = NULL`，则 `rs.getDate("due_date")` 返回 Java 的 `null`。

此时不能执行 `date.toLocalDate()`，否则会抛出 `NullPointerException`。

- `date != null` 为 `true`：有日期，转换成 `LocalDate`。
- `date != null` 为 `false`：没有日期，保留 `dueDate = null`。

| 数据库 due_date | rs.getDate() | 是否 toLocalDate() | 最终 dueDate |
| --- | --- | --- | --- |
| NULL | null | 否 | null |
| 2026-11-15 | java.sql.Date 对象 | 是 | 2026-11-15 对应的 LocalDate |

### 声明类型不等于实际引用值

```java
LocalDate dueDate = null;
String name = null;
Task task = null;
```

这些变量的**声明类型没有改变**；`null` 只表示当前没有引用对象。

**有日期才转换；没有日期就保留 null，Task 可以表示“没有截止日期”。**

## 7. Task 类的三个构造方法（复习重点）

三个构造方法的目的不同，涉及**构造方法重载**。

### 第一个：创建普通新任务

```java
public Task(String title, Priority priority, TaskStatus status) {
    this(nextId, title, priority, status);
    nextId++;
}
```

作用：通过第三个构造方法初始化数据，使用 `nextId` 自动分配 ID，然后 `nextId++`。

### 第二个：创建带日期的新任务

```java
public Task(String title, Priority priority, TaskStatus status, LocalDate dueDate) {
    this(title, priority, status);
    this.dueDate = dueDate;
}
```

作用：先调用第一个构造方法，自动分配 ID，再设置截止日期。

### 第三个：使用指定 ID 初始化

```java
private Task(int id, String title, Priority priority, TaskStatus status) {
    // 检查 id、title、priority、status 是否合法
    this.id = id;
    this.title = title;
    this.priority = priority;
    this.status = status;
}
```

作用：校验参数，根据传入的 ID 初始化基本字段；**本身不增加 nextId**。由于是 `private`，只能在 Task 类内部调用。

构造方法调用顺序：

```text
第二个构造方法
    ↓ this(title, priority, status)
第一个构造方法
    ↓ this(nextId, title, priority, status)
第三个构造方法
```

例如最初 `nextId = 5`，使用第二个构造方法创建任务，最终新任务 ID 为 5，`nextId` 变为 6。

## 8. Task.restore()：恢复已有任务（复习重点）

`Task.restore()` 是 TaskHub 自己定义的方法，**不是 JDBC 自带的方法**。

| 方法 | 使用场景 | ID 处理 |
| --- | --- | --- |
| `new Task(...)` | 创建新任务 | 自动分配 ID |
| `Task.restore(...)` | 恢复已有任务 | 保留传入的原始 ID |

例如数据库中的任务 ID 为 20。如果读取时使用普通构造方法，生成的 ID 可能不同，以后根据 ID 修改、删除数据库记录时就有操作错任务的风险。

调用方式：

```java
Task task = Task.restore(
    20, "学习 JDBC", Priority.HIGH, TaskStatus.TODO, null, null
);
```

六个参数依次是：`id`、`title`、`priority`、`status`、`tags`、`dueDate`。

### 核心内部逻辑

```java
Task task = new Task(id, title, priority, status);
task.dueDate = dueDate;

// tags 不为 null 时逐个恢复标签
nextId = Math.max(nextId, id + 1);

return task;
```

- 通过第三个构造方法，使用指定的原始 ID。
- 恢复截止日期和标签。
- 更新 Java 的 `nextId`，避免后续新建 Java 任务时复用已恢复 ID。
- 返回完整的 `Task` 对象。

例如：初始 `nextId = 5`，恢复 ID 8 后为 9，继续恢复 ID 12 后为 13。

注意：**Java 的 nextId 与 MySQL 的 AUTO_INCREMENT 是两个独立的机制。**

当前 JDBC 查询没有 `tags` 字段，`restore()` 的标签参数传 `null`；由于 Task 字段本来初始化为空 `HashSet`，恢复后的标签集合仍为空集合。

**本阶段只需掌握用途、参数与返回结果，不必背诵 restore() 的所有内部代码。**

## 9. mapRow()：将当前结果行转换为 Task

`mapRow()` 是我们在 `JdbcQueryDemo` 中自己编写的方法，不是 JDBC 自带方法。

```java
private static Task mapRow(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String title = rs.getString("title");

    Priority priority = Priority.valueOf(rs.getString("priority"));
    TaskStatus status = TaskStatus.valueOf(rs.getString("status"));

    Date date = rs.getDate("due_date");
    LocalDate dueDate = null;
    if (date != null) {
        dueDate = date.toLocalDate();
    }

    return Task.restore(id, title, priority, status, null, dueDate);
}
```

外部调用：

```java
while (rs.next()) {
    Task task = mapRow(rs);
    System.out.println(
        "任务ID：" + task.getId()
        + "，任务名称：" + task.getTitle()
        + "，任务状态：" + task.getStatus()
    );
}
```

### 职责分离

- 外部 `while (rs.next())`：逐行移动游标。
- `mapRow(rs)`：读取当前行、转换字段、生成 Task 对象。
- `Task.restore()`：保留数据库原始 ID，恢复对象。

**mapRow() 内部不要再调用 next()，否则可能跳过记录。**

查询返回 3 行时，`next()` 会调用 4 次，`mapRow()` 只调用 3 次，产生 3 个 Task 对象。

## 10. try-with-resources 和异常处理

```java
try (
    Connection conn = DriverManager.getConnection(url, username, password);
    PreparedStatement ps = conn.prepareStatement(sql)
) {
    ps.setInt(1, 10);

    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Task task = mapRow(rs);
            System.out.println(task.getTitle());
        }
    }
}
```

### 为什么用 try-with-resources？

- 正常执行结束后自动关闭资源。
- 执行过程中发生异常，也会尝试关闭已经成功创建的资源。
- 本例不需要分别手动调用 `rs.close()`、`ps.close()`、`conn.close()`。

本例关闭顺序：**ResultSet → PreparedStatement → Connection**。

### throws SQLException 不是处理异常

```java
public static void main(String[] args) throws SQLException
```

表示当前方法可能产生 `SQLException`，允许异常向外传播，**并不是一定发生异常，更不是已经处理了异常**。

| 语法 | 含义 |
| --- | --- |
| `throw` | 主动抛出异常对象 |
| `throws` | 声明异常可以向外传播 |
| `try-catch` | 捕获并处理异常 |
| `try-with-resources` | 自动关闭实现 AutoCloseable 的资源 |

`try-with-resources` 负责资源管理，不能把它理解成“自动吞掉异常”。

## 11. Day 38 实际实验与排错

### 1. IntelliJ IDEA 的 SQL 编辑器提示

曾显示“无法解析表 tasks / 列 id、title 等”，可能因为 IDEA SQL 静态检查没有关联正确的数据源。这类编辑器提示不等同于运行时 SQL 错误，需以实际执行和异常信息为准。

### 2. 连接成功后执行查询

先打印：

```text
SQL 查询执行成功
```

再通过 `while(rs.next())` 打印任务标题，确认 ResultSet 可以读取数据。

### 3. 完成 mapRow() 后的真实运行结果

```text
SQL 查询执行成功
任务ID：2，任务名称：学习 MySQL，任务状态：TODO
任务ID：3，任务名称：阅读论文，任务状态：IN_PROGRESS
任务ID：4，任务名称：完成Java作业，任务状态：TODO

进程已结束，退出代码为 0
```

说明：

1. JDBC 连接成功。
2. PreparedStatement 查询成功执行。
3. ResultSet 正常逐行遍历。
4. 枚举和日期字段映射没有引发错误。
5. `mapRow()` 与 `Task.restore()` 正常恢复任务。
6. 输出的任务 ID 保留了数据库原始编号。

当前只实现 **SELECT 查询和对象映射**，还没有实现 JDBC 的增删改或替换 TaskRepository。

## 12. JDBC 代码审查清单

审查 Codex 或自己编写的 JDBC 代码时，重点检查：

1. 数据库 URL 是否指向正确的库和表？
2. 数据库密码是否被硬编码或误提交？
3. SQL 是否正确使用 `?` 参数绑定？
4. 参数编号和绑定顺序是否正确？
5. 是否调用 `executeQuery()` 真正执行 SELECT？
6. 是否先执行 `rs.next()` 再读取字段？
7. `mapRow()` 是否错误地再次移动游标？
8. 枚举字符串与实际枚举常量是否完全匹配？
9. 日期转换前是否正确处理 `null`？
10. 恢复已有 Task 时是否保留原始 ID？
11. 是否通过 try-with-resources 管理 JDBC 资源？
12. 是否区分 SQL 异常和 Java 类型转换异常？

**代码能运行，不等于所有数据映射和资源管理逻辑都一定正确。**

## 今天的核心总结

1. JDBC 核心流程是：Connection → PreparedStatement → ResultSet → mapRow() → Task。
2. **prepareStatement() 准备 SQL，executeQuery() 真正执行查询。**
3. 参数占位符 `?` 从编号 1 开始绑定；LIMIT 表示最多返回指定行数。
4. **ResultSet 游标初始在第一行之前，next() 负责移动游标并返回 true/false。**
5. 3 行数据的完整 while 遍历中，next() 执行 4 次，循环体执行 3 次。
6. getInt()、getString() 和 getDate() 读取当前行，不会移动游标。
7. 枚举需要通过 valueOf() 从字符串转换，且区分大小写。
8. **SQL NULL 读取为 Java null，不需要也不能对 null 调用 toLocalDate()。**
9. 普通 Task 构造方法负责创建新任务，Task.restore() 保留已有任务的原始 ID。
10. **while 负责换行，mapRow() 负责映射当前行。**
11. try-with-resources 自动关闭资源；throws SQLException 声明异常可以向外传播。
12. 已成功查询 `taskhub.tasks` 并恢复成包含原始 ID、名称和状态的 Task 对象。

## 待完成事项

- 巩固 `throws`、`throw`、`try-catch` 与 try-with-resources 的区别。
- 后续学习 JDBC INSERT、UPDATE、DELETE。
- 后续将 JDBC 实现整合进 TaskRepository。
