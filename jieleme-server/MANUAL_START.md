# 手动启动服务端指南

## 📋 前提条件

✅ Java 17 已安装（已检测到）  
⚠️ Maven 未在 PATH 中（需要手动处理）  
⚠️ MySQL 需要手动初始化数据库  

---

## 🎯 启动步骤

### 步骤 1: 初始化数据库（必须）

#### 使用 MySQL 客户端工具（推荐）⭐

1. **打开 MySQL Workbench / Navicat / HeidiSQL**

2. **连接到 MySQL**
   - Host: localhost
   - Port: 3306
   - Username: root
   - Password: root（或你的实际密码）

3. **执行初始化脚本**
   
   **方式 A: 打开文件执行**
   - File → Open SQL Script
   - 选择：`D:\flutterspace\companion\spring-code\spring-code\jieleme-server\init-database.sql`
   - 点击执行按钮 ⚡

   **方式 B: 复制粘贴**
   - 打开 `init-database.sql` 文件
   - 复制所有内容
   - 在 MySQL 客户端的查询窗口粘贴
   - 执行

4. **验证创建成功**
   ```sql
   SHOW DATABASES LIKE 'jieleme';
   USE jieleme;
   SHOW TABLES;
   ```
   应该看到 `t_user` 和 `t_sms_code` 两个表

---

### 步骤 2: 确认数据库密码

打开 `src\main\resources\application.yml`，检查密码是否正确：

```yaml
spring:
  datasource:
    username: root
    password: root  # ← 如果你的 MySQL 密码不是 root，请修改这里
```

---

### 步骤 3: 启动服务

#### 方式 A: 使用 IDEA（最简单）⭐⭐⭐

1. **用 IDEA 打开项目**
   - File → Open
   - 选择 `jieleme-server` 文件夹
   - 等待 Maven 导入依赖

2. **找到主类**
   - 打开 `src/main/java/com/jieleme/JielemeServerApplication.java`

3. **运行**
   - 右键文件 → Run 'JielemeServerApplication'
   - 或点击行号旁边的绿色 ▶️ 按钮

#### 方式 B: 使用 Maven Wrapper（推荐）⭐⭐

如果项目有 `mvnw` 或 `mvnw.cmd`：

```powershell
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server
.\mvnw.cmd spring-boot:run
```

#### 方式 C: 使用已安装的 Maven⭐

如果你已经安装了 Maven：

```powershell
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server
mvn spring-boot:run
```

#### 方式 D: 手动编译运行

```powershell
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server

# 编译
mvn clean package -DskipTests

# 运行
java -jar target\jieleme-server-1.0.0.jar
```

---

### 步骤 4: 验证启动成功

#### 看到这些日志表示成功

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.1)

Started JielemeServerApplication in 3.456 seconds
```

看到 **"Started JielemeServerApplication"** 就成功了！✅

#### 测试接口

打开新的 PowerShell 窗口：

```powershell
# 测试发送验证码
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

---

## 🐛 常见问题排查

### 问题 1: "Unknown database 'jieleme'"

**原因**：数据库未创建

**解决**：
1. 打开 MySQL 客户端
2. 执行 `init-database.sql`
3. 验证：`SHOW DATABASES LIKE 'jieleme';`

---

### 问题 2: "Access denied for user 'root'"

**原因**：数据库密码错误

**解决**：
1. 确认你的 MySQL 密码
2. 修改 `application.yml` 中的 `password`
3. 重启服务

---

### 问题 3: "Port 8080 was already in use"

**原因**：端口被占用

**解决方式 1**：结束占用进程
```powershell
# 查找占用端口的进程
netstat -ano | findstr 8080

# 结束进程（替换 12345 为实际 PID）
taskkill /PID 12345 /F
```

**解决方式 2**：修改端口
```yaml
# 编辑 application.yml
server:
  port: 8081  # 改成其他端口
```

---

### 问题 4: "Table 't_user' doesn't exist"

**原因**：表未创建

**解决**：
1. 确认数据库 `jieleme` 已创建
2. 执行 `init-database.sql` 中的建表语句
3. 验证：`USE jieleme; SHOW TABLES;`

---

### 问题 5: Maven 依赖下载很慢

**解决**：配置阿里云镜像

创建文件 `C:\Users\Administrator\.m2\settings.xml`：

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

## 🎯 推荐启动方式

### 🥇 最简单：使用 IDEA

1. 用 IDEA 打开 `jieleme-server` 文件夹
2. 等待 Maven 导入依赖
3. 运行 `JielemeServerApplication`

**优点**：
- ✅ 自动管理依赖
- ✅ 一键启动
- ✅ 方便调试
- ✅ 可以看日志

---

### 🥈 次选：使用启动脚本

```powershell
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server
.\start.ps1
```

或双击 `start.bat` 文件

---

### 🥉 备选：命令行

```powershell
cd D:\flutterspace\companion\spring-code\spring-code\jieleme-server
mvn spring-boot:run
```

---

## ✅ 启动检查清单

启动前确认：

- [ ] ✅ Java 17 已安装（已确认）
- [ ] ⚠️ MySQL 服务已启动
- [ ] ⚠️ 数据库 `jieleme` 已创建
- [ ] ⚠️ 表 `t_user` 和 `t_sms_code` 已创建
- [ ] ⚠️ `application.yml` 密码正确
- [ ] ⚠️ 端口 8080 未被占用

---

## 📞 需要帮助？

### 我来帮你

告诉我：
1. 你使用的 MySQL 客户端工具（Workbench / Navicat / 其他）
2. 你的 MySQL 密码是否是 `root`
3. 你想用哪种方式启动（IDEA / 命令行）

我会给你更具体的指导！

---

**下一步**：先初始化数据库，然后我帮你启动服务！
