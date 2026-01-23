# 服务端启动指南

## 🚀 快速启动（3步）

### 步骤 1: 初始化数据库

#### 方式 A: 使用 MySQL Workbench / Navicat（推荐）

1. **打开 MySQL 客户端工具**
2. **连接到 MySQL**（用户名: root，密码: root）
3. **打开并执行** `init-database.sql` 文件

**在 MySQL Workbench 中**：
- File → Open SQL Script → 选择 `init-database.sql`
- 点击 ⚡ 执行按钮

**在 Navicat 中**：
- 右键数据库 → Execute SQL File → 选择 `init-database.sql`

#### 方式 B: 使用命令行

如果你的系统有 MySQL 命令行工具：

```bash
# 假设 MySQL 安装在默认位置
# Windows (可能的路径)
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot < init-database.sql

# 或者先进入 MySQL
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot
# 然后执行
SOURCE D:\flutterspace\companion\spring-code\spring-code\jieleme-server\init-database.sql;
```

#### 方式 C: 手动执行（最简单）

复制 `init-database.sql` 的内容，在 MySQL 客户端中直接粘贴执行。

---

### 步骤 2: 验证数据库

执行完成后，应该看到：

```sql
-- 查看数据库
SHOW DATABASES;
-- 应该能看到 jieleme

-- 查看表
USE jieleme;
SHOW TABLES;
-- 应该能看到 t_user 和 t_sms_code
```

---

### 步骤 3: 启动 Spring Boot 服务

```bash
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server
mvn spring-boot:run
```

或者使用启动脚本（见下文）

---

## 📝 详细步骤

### 1. 检查 MySQL 服务是否运行

#### Windows

打开"服务"（services.msc）：
- 查找 "MySQL80" 或类似服务
- 确保状态为"正在运行"
- 如果未运行，右键启动

或使用命令：
```powershell
Get-Service -Name "*MySQL*"
```

---

### 2. 确认数据库密码

当前配置的密码是 `root`。

如果你的 MySQL 密码不是 `root`，需要修改 `application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: 你的实际密码  # 修改这里
```

---

### 3. 创建数据库和表

#### 选项 1: 使用 MySQL Workbench（推荐）⭐

1. 打开 MySQL Workbench
2. 连接到本地 MySQL（localhost, root）
3. 点击左上角的 📄 打开 SQL 脚本图标
4. 选择 `init-database.sql`
5. 点击 ⚡ 执行（或按 Ctrl+Shift+Enter）
6. 检查是否执行成功（左侧应该能看到 jieleme 数据库）

#### 选项 2: 使用 Navicat

1. 打开 Navicat
2. 连接到本地 MySQL
3. 点击"查询" → "新建查询"
4. 复制 `init-database.sql` 的内容粘贴进去
5. 点击"运行"
6. 刷新左侧，应该能看到 jieleme 数据库

#### 选项 3: 使用 HeidiSQL

1. 打开 HeidiSQL
2. 连接到 MySQL
3. File → Load SQL file → 选择 `init-database.sql`
4. F9 执行
5. 刷新数据库列表

#### 选项 4: 手动复制粘贴

1. 打开任何 MySQL 客户端
2. 打开 `init-database.sql` 文件
3. 复制所有内容
4. 在 MySQL 客户端的查询窗口粘贴
5. 执行

---

### 4. 验证数据库创建成功

在 MySQL 客户端执行：

```sql
-- 查看数据库
SHOW DATABASES LIKE 'jieleme';
-- 应该显示: jieleme

-- 切换到数据库
USE jieleme;

-- 查看表
SHOW TABLES;
-- 应该显示:
-- t_user
-- t_sms_code

-- 查看用户表结构
DESC t_user;

-- 查看验证码表结构
DESC t_sms_code;
```

如果都能正常显示，说明数据库初始化成功！✅

---

### 5. 启动 Spring Boot 服务

```bash
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server
mvn spring-boot:run
```

---

## ✅ 成功标志

启动成功后，你应该看到类似的日志：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.1)

2026-01-23 ... : Starting JielemeServerApplication
2026-01-23 ... : Started JielemeServerApplication in 3.456 seconds (JVM running for 4.123)
```

看到 **"Started JielemeServerApplication"** 表示启动成功！

---

## 🧪 测试服务是否正常

打开新的 PowerShell 窗口，执行：

```powershell
# 测试发送验证码接口
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/sendCode" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"phone":"13800138000","scene":"login"}'
```

预期响应：
```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "requestId": "...",
    "expireSeconds": 300
  }
}
```

或运行完整测试脚本：
```powershell
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server
.\test.ps1
```

---

## 🐛 常见问题

### 问题 1: "Cannot find MySQL"

**解决方法**：MySQL 命令行不在 PATH 中，使用图形化工具（MySQL Workbench）代替。

### 问题 2: "Access denied for user 'root'"

**原因**：数据库密码不正确

**解决方法**：
1. 确认你的 MySQL 密码
2. 修改 `application.yml` 中的 `password`

### 问题 3: "Port 8080 was already in use"

**原因**：端口被占用

**解决方法**：
```powershell
# 查看占用端口的进程
netstat -ano | findstr 8080

# 结束进程（替换 PID）
taskkill /PID 进程号 /F

# 或修改端口
# 编辑 application.yml，将 port: 8080 改为其他端口
```

### 问题 4: Maven 下载依赖很慢

**解决方法**：配置阿里云镜像

创建或编辑 `C:\Users\你的用户名\.m2\settings.xml`：

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

---

## 🎯 启动检查清单

在启动前确认：

- [ ] ✅ MySQL 服务已启动
- [ ] ✅ 数据库 `jieleme` 已创建
- [ ] ✅ 表 `t_user` 和 `t_sms_code` 已创建
- [ ] ✅ `application.yml` 中的数据库密码正确
- [ ] ✅ JDK 17 已安装
- [ ] ✅ Maven 已安装

---

**准备好后，告诉我你到哪一步了，我继续帮你！** 🚀
