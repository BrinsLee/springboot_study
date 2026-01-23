# 戒了么服务端 - 手机号注册登录

## 项目简介

基于 Spring Boot + MyBatis + MySQL 实现的手机号注册/登录系统。

### 技术栈

- **Spring Boot 3.2.1** - Web框架
- **MyBatis 3.0.3** - ORM框架
- **MySQL 8.x** - 数据库
- **JWT (jjwt 0.12.3)** - Token认证
- **Java 17** - 开发语言

### 核心功能

1. 手机号验证码发送（测试模式固定验证码：123456）
2. 手机号验证码登录
3. 自动注册（首次登录自动创建用户）
4. JWT Token鉴权
5. 获取当前用户信息

## 快速开始

### 1. 环境准备

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库初始化

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 执行建表脚本
source src/main/resources/schema.sql
```

或手动执行：

```sql
CREATE DATABASE IF NOT EXISTS jieleme DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jieleme;

-- 执行 schema.sql 中的建表语句
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/jieleme?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your_password  # 修改为你的数据库密码
```

### 4. 启动项目

```bash
# 方式1：使用Maven
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/jieleme-server-1.0.0.jar
```

启动成功后，服务运行在 `http://localhost:8080`

## API 接口文档

### 基础信息

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`

### 统一响应格式

```json
{
  "code": 0,
  "msg": "ok",
  "data": {}
}
```

### 错误码说明

| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 1001 | 手机号格式不合法 |
| 1002 | 验证码错误 |
| 1003 | 验证码过期 |
| 1004 | 验证码请求过于频繁 |
| 1005 | 用户被禁用 |
| 2000 | 系统异常 |
| 4010 | 未授权 |

### 接口列表

#### 1. 发送验证码

**请求**

```http
POST /api/v1/auth/sendCode
Content-Type: application/json

{
  "phone": "13800138000",
  "scene": "login"
}
```

**响应**

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "requestId": "b0d4d4b6f5b54a5cae6fbb6b0b91d2c1",
    "expireSeconds": 300
  }
}
```

**限流规则**
- 同手机号60秒内最多发送1次
- 同手机号1小时内最多发送5次

#### 2. 登录（自动注册）

**请求**

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "phone": "13800138000",
  "code": "123456",
  "requestId": "b0d4d4b6f5b54a5cae6fbb6b0b91d2c1"
}
```

- `requestId` 可选，测试阶段可不传

**响应**

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "token": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 2592000,
    "user": {
      "id": 1,
      "phone": "13800138000",
      "nickname": "温柔的海豚4821",
      "createdAt": "2026-01-23T10:00:00Z"
    }
  }
}
```

**说明**
- 如果手机号不存在，自动创建新用户
- 昵称自动生成：`形容词 + 的 + 动物 + 4位随机数`
- Token有效期30天

#### 3. 获取当前用户信息

**请求**

```http
GET /api/v1/user/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应**

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "id": 1,
    "phone": "13800138000",
    "nickname": "温柔的海豚4821",
    "createdAt": "2026-01-23T10:00:00Z",
    "lastLoginAt": "2026-01-23T10:05:00Z"
  }
}
```

## 测试用例

### 用例1：新用户登录自动注册

```bash
# 1. 发送验证码
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","scene":"login"}'

# 2. 登录（首次登录会自动注册）
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"123456"}'

# 预期结果：
# - 返回token和新创建的用户信息
# - 用户昵称自动生成
# - created_at和last_login_at相同
```

### 用例2：老用户登录

```bash
# 使用已存在的手机号登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"123456"}'

# 预期结果：
# - 不创建新用户
# - 更新last_login_at
# - 返回token和用户信息
```

### 用例3：验证码限流

```bash
# 连续多次发送验证码
for i in {1..3}; do
  curl -X POST http://localhost:8080/api/v1/auth/sendCode \
    -H "Content-Type: application/json" \
    -d '{"phone":"13800138000","scene":"login"}'
  sleep 1
done

# 预期结果：
# - 第1次成功
# - 第2次失败，返回错误码1004（60秒内不能重复发送）
# - 第3次失败，返回错误码1004
```

### 用例4：获取用户信息（需要鉴权）

```bash
# 不带token
curl -X GET http://localhost:8080/api/v1/user/me

# 预期结果：
# - HTTP 401
# - 返回错误码4010（未授权）

# 带token
curl -X GET http://localhost:8080/api/v1/user/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 预期结果：
# - 返回当前用户信息
```

## 项目结构

```
src/main/java/com/jieleme/
├── JielemeServerApplication.java       # 启动类
├── common/                             # 通用模块
│   ├── api/
│   │   ├── ApiResponse.java           # 统一响应
│   │   └── ErrorCode.java             # 错误码
│   ├── exception/
│   │   ├── BizException.java          # 业务异常
│   │   └── GlobalExceptionHandler.java # 全局异常处理
│   └── util/
│       ├── JwtUtil.java               # JWT工具
│       ├── NicknameGenerator.java     # 昵称生成器
│       └── PhoneUtil.java             # 手机号工具
├── config/                             # 配置模块
│   ├── AuthInterceptor.java          # 认证拦截器
│   └── WebMvcConfig.java             # MVC配置
├── module/
│   ├── auth/                          # 认证模块
│   │   ├── controller/
│   │   │   └── AuthController.java
│   │   ├── dto/
│   │   │   ├── LoginReq.java
│   │   │   ├── LoginResp.java
│   │   │   └── SendCodeReq.java
│   │   └── service/
│   │       ├── AuthService.java
│   │       └── impl/
│   │           └── AuthServiceImpl.java
│   ├── user/                          # 用户模块
│   │   ├── controller/
│   │   │   └── UserController.java
│   │   ├── entity/
│   │   │   └── User.java
│   │   ├── mapper/
│   │   │   └── UserMapper.java
│   │   └── service/
│   │       ├── UserService.java
│   │       └── impl/
│   │           └── UserServiceImpl.java
│   └── sms/                           # 短信模块
│       ├── entity/
│       │   └── SmsCode.java
│       ├── mapper/
│       │   └── SmsCodeMapper.java
│       └── service/
│           ├── SmsCodeService.java
│           └── impl/
│               └── SmsCodeServiceImpl.java
└── src/main/resources/
    ├── application.yml                 # 配置文件
    ├── schema.sql                     # 建表脚本
    └── mapper/                        # MyBatis XML
        ├── UserMapper.xml
        └── SmsCodeMapper.xml
```

## 配置说明

### application.yml 配置项

```yaml
# 自定义认证配置
auth:
  jwt-secret: "jieleme_secret_key_change_me_in_production_2026"  # JWT签名密钥（生产环境必须修改）
  token-expire-seconds: 2592000      # Token过期时间（秒），默认30天
  test-mode: true                     # 测试模式（固定验证码123456）
  sms-expire-seconds: 300            # 验证码过期时间（秒），默认5分钟
  sms-min-interval-seconds: 60       # 验证码最小发送间隔（秒）
  sms-max-per-hour: 5                # 每小时最大发送次数
```

### 生产环境注意事项

1. **修改JWT密钥**：`auth.jwt-secret` 必须使用强随机字符串
2. **关闭测试模式**：`auth.test-mode: false`
3. **接入真实短信服务**：修改 `SmsCodeServiceImpl` 中的发送逻辑
4. **启用HTTPS**：保护Token传输安全
5. **配置日志级别**：生产环境建议 `logging.level.com.jieleme: info`

## 常见问题

### Q1: 启动报错 "Access denied for user 'root'@'localhost'"

**A**: 数据库密码错误，请修改 `application.yml` 中的 `spring.datasource.password`

### Q2: 启动报错 "Unknown database 'jieleme'"

**A**: 数据库不存在，请先执行 `schema.sql` 创建数据库和表

### Q3: Token验证失败

**A**: 
1. 检查请求头是否正确：`Authorization: Bearer YOUR_TOKEN`
2. Token是否过期（默认30天）
3. JWT密钥是否被修改

### Q4: 如何修改验证码固定值？

**A**: 修改 `SmsCodeServiceImpl` 中的 `TEST_CODE` 常量

### Q5: 如何接入真实短信服务？

**A**: 
1. 设置 `auth.test-mode: false`
2. 修改 `SmsCodeServiceImpl.sendCode()` 方法
3. 集成阿里云/腾讯云短信SDK

## 开发计划

- [ ] 集成真实短信服务
- [ ] 添加用户信息修改接口
- [ ] 添加找回密码功能
- [ ] 支持第三方登录（微信、QQ等）
- [ ] 添加完整的单元测试
- [ ] 添加接口文档（Swagger/Knife4j）

## 许可证

MIT License
