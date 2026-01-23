# 如何在 IDE 中打开项目

## 📁 当前项目结构

```
spring-code/
├── jieleme-server/          ← 这是主项目（请打开这个目录）
│   ├── src/
│   ├── pom.xml             ← Maven 配置文件
│   ├── README.md
│   └── ...
├── README.md               ← 项目说明文档
└── .gitignore
```

## ✅ 正确的打开方式

### IntelliJ IDEA

#### 方法1: 通过菜单打开（推荐）

1. **打开 IDEA**
2. 点击 **File → Open**
3. 浏览到 `D:\flutterspace\companion\spring-code\spring-code\jieleme-server`
4. 选择 **jieleme-server** 文件夹
5. 点击 **OK**
6. 等待 Maven 自动导入依赖

#### 方法2: 直接拖拽

1. 打开文件资源管理器
2. 找到 `jieleme-server` 文件夹
3. 拖拽到 IDEA 窗口
4. 选择 "Open as Project"

#### 验证项目已正确打开

✅ 左侧项目树显示如下结构：
```
jieleme-server
├── src
│   └── main
│       └── java
│           └── com.jieleme
│               ├── JielemeServerApplication  ← 可以看到主类
│               ├── common
│               ├── config
│               └── module
├── External Libraries         ← Maven 依赖已加载
└── pom.xml
```

✅ 右下角显示 "Maven projects need to be imported"
   - 点击 "Import Changes" 或 "Enable Auto-Import"

✅ 可以看到 JDK 配置
   - File → Project Structure → Project
   - SDK 应该是 JDK 17

### Eclipse

1. **打开 Eclipse**
2. 点击 **File → Import**
3. 选择 **Maven → Existing Maven Projects**
4. **Root Directory**: 浏览到 `D:\flutterspace\companion\spring-code\spring-code\jieleme-server`
5. 确认勾选了 `pom.xml`
6. 点击 **Finish**

### VS Code

1. **打开 VS Code**
2. 点击 **File → Open Folder**
3. 浏览到 `D:\flutterspace\companion\spring-code\spring-code\jieleme-server`
4. 选择 **jieleme-server** 文件夹
5. VS Code 会自动识别 Maven 项目

**需要的插件**：
- Extension Pack for Java
- Spring Boot Extension Pack

## ❌ 错误的打开方式

### ❌ 不要打开 spring-code 目录

```
spring-code/          ← ❌ 不要打开这个目录！
├── jieleme-server/  ← ✅ 应该打开这个
└── ...
```

**原因**：
- `spring-code` 不包含有效的 `pom.xml`
- IDE 无法识别为 Maven 项目
- 无法导入依赖
- 无法运行代码

## 🚀 启动项目

### 在 IDEA 中启动

1. 找到 `JielemeServerApplication.java`
   ```
   src/main/java/com/jieleme/JielemeServerApplication.java
   ```

2. 右键点击文件
3. 选择 **Run 'JielemeServerApplication'**

或者：
- 打开文件后，点击行号旁边的绿色运行按钮 ▶️

### 在 Eclipse 中启动

1. 右键 `JielemeServerApplication.java`
2. 选择 **Run As → Java Application**

### 在 VS Code 中启动

1. 打开 `JielemeServerApplication.java`
2. 点击右上角的 **Run** 按钮
3. 或按 **F5**

## 🔧 配置检查清单

启动前请确保：

- [ ] ✅ 打开的是 `jieleme-server` 目录
- [ ] ✅ Maven 依赖已全部下载完成
- [ ] ✅ JDK 配置为 JDK 17
- [ ] ✅ MySQL 数据库已安装并启动
- [ ] ✅ 数据库 `jieleme` 已创建
- [ ] ✅ 建表脚本已执行
- [ ] ✅ `application.yml` 中的数据库密码已修改

## 🐛 常见问题

### 问题1: IDE 无法识别项目

**现象**：
- 看不到 Maven 项目结构
- 代码没有高亮
- 无法导入类

**解决方法**：
1. 确认打开的是 `jieleme-server` 目录
2. 手动导入 Maven：右键 `pom.xml` → Maven → Reimport
3. 刷新项目：File → Invalidate Caches / Restart

### 问题2: 找不到主类

**现象**：
- 无法运行 `JielemeServerApplication`
- 提示 "Cannot find main class"

**解决方法**：
1. 确认 JDK 配置正确（File → Project Structure → SDK）
2. 重新编译：Build → Rebuild Project
3. 清理缓存：File → Invalidate Caches / Restart

### 问题3: Maven 依赖下载失败

**现象**：
- External Libraries 为空
- 导入类报错

**解决方法**：
配置国内镜像，编辑 `~/.m2/settings.xml`：

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

然后重新导入：右键 `pom.xml` → Maven → Reload Project

### 问题4: 启动报错 "Access denied for user"

**现象**：
- 启动时数据库连接失败

**解决方法**：
检查 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码  # 修改这里
```

## 📞 需要帮助？

1. 📖 查看 [完整文档](jieleme-server/README.md)
2. 🚀 查看 [快速启动指南](jieleme-server/QUICK_START.md)
3. 🧪 查看 [API测试文档](jieleme-server/API_TEST.md)

## 🎯 快速验证

启动成功后，应该看到：

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

测试接口：
```bash
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","scene":"login"}'
```

---

**重要提示**: 始终打开 `jieleme-server` 目录，不是 `spring-code` 目录！
