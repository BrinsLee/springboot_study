# 快速启动指南

## 前置条件

- ✅ JDK 17 或更高版本
- ✅ Maven 3.6 或更高版本
- ✅ MySQL 8.0 或更高版本
- ✅ Git（可选）

## 5分钟快速启动

### 步骤1: 创建数据库（2分钟）

打开MySQL命令行或工具（如Navicat、DBeaver），执行：

```sql
CREATE DATABASE IF NOT EXISTS jieleme DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 步骤2: 执行建表脚本（1分钟）

```bash
# 方式1: 在MySQL命令行中执行
mysql -u root -p jieleme < src/main/resources/schema.sql

# 方式2: 手动复制 schema.sql 内容到MySQL客户端执行
```

或者直接在MySQL客户端中执行 `src/main/resources/schema.sql` 文件的内容。

### 步骤3: 修改数据库密码（30秒）

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码  # 修改这里
```

### 步骤4: 启动服务（1分钟）

#### 方式A: 使用Maven（推荐）

```bash
# 在项目根目录执行
mvn spring-boot:run
```

#### 方式B: IDEA/Eclipse

1. 打开项目
2. 找到 `JielemeServerApplication.java`
3. 右键 → Run 'JielemeServerApplication'

#### 方式C: 打包后运行

```bash
mvn clean package
java -jar target/jieleme-server-1.0.0.jar
```

### 步骤5: 验证启动（30秒）

看到以下日志表示启动成功：

```
Started JielemeServerApplication in 3.456 seconds
```

测试接口：

```bash
# Windows PowerShell
curl http://localhost:8080/api/v1/auth/sendCode -Method Post -ContentType "application/json" -Body '{"phone":"13800138000","scene":"login"}'

# Linux/Mac
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","scene":"login"}'
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

## 完整测试流程

### 测试1: 发送验证码 + 登录

```bash
# 1. 发送验证码
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","scene":"login"}'

# 2. 登录（验证码固定为 123456）
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"123456"}'

# 响应中会包含 token，格式为：
# "token": "Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 测试2: 获取用户信息

```bash
# 使用上一步获取的token（注意替换YOUR_TOKEN_HERE）
curl -X GET http://localhost:8080/api/v1/user/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 使用测试脚本（推荐）

#### Windows PowerShell

```powershell
# 运行测试脚本
.\test.ps1
```

#### Linux/Mac

```bash
# 给脚本执行权限
chmod +x test.sh

# 运行测试脚本
./test.sh
```

测试脚本会自动执行所有测试用例，包括：
- ✅ 发送验证码
- ✅ 登录（自动注册）
- ✅ 鉴权拦截
- ✅ 获取用户信息
- ✅ 频率限制
- ✅ 验证码校验
- ✅ 格式校验

## 常见问题排查

### 问题1: 启动失败 - 数据库连接错误

**错误信息**：
```
Access denied for user 'root'@'localhost' (using password: YES)
```

**解决方法**：
1. 检查 `application.yml` 中的用户名密码是否正确
2. 确认MySQL服务是否启动

### 问题2: 启动失败 - 端口被占用

**错误信息**：
```
Port 8080 was already in use
```

**解决方法**：

方式1: 修改端口（推荐）
```yaml
# application.yml
server:
  port: 8081  # 改成其他端口
```

方式2: 杀掉占用端口的进程
```bash
# Windows
netstat -ano | findstr 8080
taskkill /PID 进程号 /F

# Linux/Mac
lsof -i :8080
kill -9 进程号
```

### 问题3: 找不到数据库 jieleme

**错误信息**：
```
Unknown database 'jieleme'
```

**解决方法**：
```sql
CREATE DATABASE jieleme DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 问题4: 表不存在

**错误信息**：
```
Table 'jieleme.t_user' doesn't exist
```

**解决方法**：
执行建表脚本 `src/main/resources/schema.sql`

### 问题5: Maven下载依赖很慢

**解决方法**：配置国内镜像

编辑 `~/.m2/settings.xml`（没有则创建）：

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

## 开发环境配置

### IDEA 配置

1. **导入项目**
   - File → Open → 选择 `jieleme-server` 目录
   - 等待Maven自动导入依赖

2. **配置JDK**
   - File → Project Structure → Project
   - SDK选择JDK 17

3. **启用Lombok**（如果报错）
   - File → Settings → Plugins
   - 搜索安装 Lombok
   - 重启IDEA

### VSCode 配置

1. **安装扩展**
   - Extension Pack for Java
   - Spring Boot Extension Pack

2. **打开项目**
   - File → Open Folder → 选择 `jieleme-server` 目录

3. **启动**
   - 按 F5 或点击 Run

## 下一步

- 📖 查看完整API文档：[README.md](README.md)
- 🧪 查看测试文档：[API_TEST.md](API_TEST.md)
- 🔧 修改配置：[application.yml](src/main/resources/application.yml)
- 💾 查看数据库结构：[schema.sql](src/main/resources/schema.sql)

## 技术支持

如有问题，请检查：
1. 日志输出（控制台）
2. 数据库连接
3. 端口占用
4. JDK版本

所有接口均返回统一格式：
```json
{
  "code": 0,      // 0表示成功，其他表示失败
  "msg": "ok",    // 提示信息
  "data": {}      // 返回数据
}
```

错误码参考：
- 1001: 手机号格式不合法
- 1002: 验证码错误
- 1004: 验证码请求过于频繁
- 2000: 系统异常
- 4010: 未授权

祝你使用愉快！🎉
