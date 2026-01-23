# Spring Code - 戒了么服务端项目

## 项目说明

本目录包含《戒了么》应用的服务端实现。

## 项目结构

```
spring-code/
├── jieleme-server/          # 戒了么服务端主项目（Spring Boot + MyBatis）
│   ├── src/
│   ├── pom.xml
│   ├── README.md           # 详细项目文档
│   ├── QUICK_START.md      # 快速启动指南
│   ├── API_TEST.md         # API测试文档
│   └── PROJECT_SUMMARY.md  # 项目交付总结
└── README.md               # 本文件
```

## 快速开始

### 方式1: 在 IntelliJ IDEA 中打开

1. **打开项目**
   ```
   File → Open → 选择 jieleme-server 目录
   ```
   > ⚠️ 注意：请选择 `jieleme-server` 目录，而不是 `spring-code` 目录

2. **等待Maven导入**
   - IDEA 会自动识别 `pom.xml` 并导入依赖
   - 右下角会显示导入进度

3. **配置JDK**
   ```
   File → Project Structure → Project → SDK
   选择 JDK 17 或更高版本
   ```

4. **运行项目**
   - 找到 `JielemeServerApplication.java`
   - 右键 → Run 'JielemeServerApplication'

### 方式2: 在 Eclipse 中打开

1. **导入项目**
   ```
   File → Import → Maven → Existing Maven Projects
   Root Directory: 选择 jieleme-server 目录
   ```

2. **等待依赖下载**

3. **运行项目**
   - 右键 `JielemeServerApplication.java`
   - Run As → Java Application

### 方式3: 在 VS Code 中打开

1. **安装插件**
   - Extension Pack for Java
   - Spring Boot Extension Pack

2. **打开文件夹**
   ```
   File → Open Folder → 选择 jieleme-server 目录
   ```

3. **运行项目**
   - 按 F5 或点击 Run 按钮

### 方式4: 命令行启动

```bash
# 进入项目目录
cd jieleme-server

# 启动服务
mvn spring-boot:run
```

## 数据库配置

在启动前，请确保：

1. **安装MySQL 8.0+**

2. **创建数据库**
   ```sql
   CREATE DATABASE jieleme DEFAULT CHARACTER SET utf8mb4;
   ```

3. **执行建表脚本**
   ```bash
   mysql -u root -p jieleme < jieleme-server/src/main/resources/schema.sql
   ```

4. **修改数据库配置**
   
   编辑 `jieleme-server/src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       username: root
       password: 你的MySQL密码  # 修改这里
   ```

## 验证启动

启动成功后访问：

```bash
# 测试接口
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

## 完整文档

详细文档请查看 `jieleme-server` 目录下的文档：

- 📖 **[README.md](jieleme-server/README.md)** - 完整项目文档
- 🚀 **[QUICK_START.md](jieleme-server/QUICK_START.md)** - 5分钟快速启动
- 🧪 **[API_TEST.md](jieleme-server/API_TEST.md)** - API测试文档
- 📋 **[PROJECT_SUMMARY.md](jieleme-server/PROJECT_SUMMARY.md)** - 项目总结

## 技术栈

- **Spring Boot 3.2.1** - Web框架
- **MyBatis 3.0.3** - ORM框架
- **MySQL 8.x** - 数据库
- **JWT (jjwt 0.12.3)** - Token认证
- **Java 17** - 开发语言

## 核心功能

✅ 手机号验证码发送（测试模式固定验证码：123456）  
✅ 手机号验证码登录  
✅ 自动注册（首次登录自动创建用户）  
✅ JWT Token鉴权  
✅ 获取当前用户信息  

## API接口

| 接口 | 方法 | 说明 |
|-----|------|-----|
| `/api/v1/auth/sendCode` | POST | 发送验证码 |
| `/api/v1/auth/login` | POST | 登录（自动注册） |
| `/api/v1/user/me` | GET | 获取当前用户信息（需鉴权） |

## 测试

项目包含自动化测试脚本：

```bash
# Windows PowerShell
cd jieleme-server
.\test.ps1

# Linux/Mac
cd jieleme-server
chmod +x test.sh
./test.sh
```

## 常见问题

### Q: IDEA 无法识别项目？

**A**: 请确保：
1. 打开的是 `jieleme-server` 目录，不是 `spring-code` 目录
2. IDEA 已安装 Maven 插件
3. 配置了 JDK 17

### Q: Maven 依赖下载失败？

**A**: 配置国内镜像，编辑 `~/.m2/settings.xml`：
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### Q: 数据库连接失败？

**A**: 检查：
1. MySQL 服务是否启动
2. 数据库 `jieleme` 是否已创建
3. `application.yml` 中的用户名密码是否正确

## 许可证

MIT License

---

**项目状态**: ✅ 已完成，可直接使用

**最后更新**: 2026-01-23
