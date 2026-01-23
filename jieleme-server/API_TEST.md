# API 测试文档

## 测试环境

- **Base URL**: `http://localhost:8080`
- **测试验证码**: `123456` (固定)

## Postman/Curl 测试用例

### 测试1: 发送验证码（成功）

#### cURL 命令

```bash
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "scene": "login"
  }'
```

#### 预期响应

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "requestId": "a1b2c3d4e5f6...",
    "expireSeconds": 300
  }
}
```

#### 验证点

- ✅ HTTP状态码 200
- ✅ code 为 0
- ✅ data包含requestId和expireSeconds
- ✅ 数据库t_sms_code表新增一条记录

---

### 测试2: 发送验证码（手机号格式错误）

#### cURL 命令

```bash
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "1234567890",
    "scene": "login"
  }'
```

#### 预期响应

```json
{
  "code": 1001,
  "msg": "手机号格式不合法"
}
```

#### 验证点

- ✅ code 为 1001
- ✅ 返回错误提示

---

### 测试3: 发送验证码（频率限制）

#### cURL 命令

```bash
# 第一次发送
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138001","scene":"login"}'

# 立即第二次发送（60秒内）
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138001","scene":"login"}'
```

#### 预期响应（第二次）

```json
{
  "code": 1004,
  "msg": "验证码发送过于频繁，请XX秒后重试"
}
```

#### 验证点

- ✅ 第一次成功
- ✅ 第二次失败，code 为 1004

---

### 测试4: 登录 - 新用户自动注册

#### cURL 命令

```bash
# 先发送验证码
curl -X POST http://localhost:8080/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900139000","scene":"login"}'

# 登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13900139000",
    "code": "123456"
  }'
```

#### 预期响应

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "token": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 2592000,
    "user": {
      "id": 1,
      "phone": "13900139000",
      "nickname": "温柔的海豚4821",
      "createdAt": "2026-01-23T10:00:00Z",
      "lastLoginAt": "2026-01-23T10:00:00Z"
    }
  }
}
```

#### 验证点

- ✅ code 为 0
- ✅ 返回token（Bearer 开头）
- ✅ user信息完整
- ✅ nickname自动生成
- ✅ 数据库t_user表新增一条记录
- ✅ created_at和last_login_at相同（首次登录）

---

### 测试5: 登录 - 老用户登录

#### cURL 命令

```bash
# 使用已存在的手机号再次登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13900139000",
    "code": "123456"
  }'
```

#### 预期响应

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "token": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 2592000,
    "user": {
      "id": 1,
      "phone": "13900139000",
      "nickname": "温柔的海豚4821",
      "createdAt": "2026-01-23T10:00:00Z",
      "lastLoginAt": "2026-01-23T10:05:00Z"
    }
  }
}
```

#### 验证点

- ✅ 用户ID不变
- ✅ 昵称不变
- ✅ last_login_at更新
- ✅ 数据库t_user表记录数不变

---

### 测试6: 登录 - 验证码错误

#### cURL 命令

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13900139000",
    "code": "999999"
  }'
```

#### 预期响应

```json
{
  "code": 1002,
  "msg": "验证码错误或已过期"
}
```

#### 验证点

- ✅ code 为 1002
- ✅ 不生成token

---

### 测试7: 获取当前用户信息 - 未授权

#### cURL 命令

```bash
curl -X GET http://localhost:8080/api/v1/user/me
```

#### 预期响应

```json
{
  "code": 4010,
  "msg": "未授权，请先登录"
}
```

#### 验证点

- ✅ HTTP状态码 401
- ✅ code 为 4010

---

### 测试8: 获取当前用户信息 - 已授权

#### cURL 命令

```bash
# 先登录获取token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900139000","code":"123456"}' | jq -r '.data.token')

# 使用token获取用户信息
curl -X GET http://localhost:8080/api/v1/user/me \
  -H "Authorization: $TOKEN"
```

#### 预期响应

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "id": 1,
    "phone": "13900139000",
    "nickname": "温柔的海豚4821",
    "createdAt": "2026-01-23T10:00:00Z",
    "lastLoginAt": "2026-01-23T10:05:00Z"
  }
}
```

#### 验证点

- ✅ HTTP状态码 200
- ✅ code 为 0
- ✅ 返回正确的用户信息

---

### 测试9: 获取当前用户信息 - Token格式错误

#### cURL 命令

```bash
curl -X GET http://localhost:8080/api/v1/user/me \
  -H "Authorization: InvalidToken"
```

#### 预期响应

```json
{
  "code": 4010,
  "msg": "未授权，请先登录"
}
```

#### 验证点

- ✅ HTTP状态码 401
- ✅ code 为 4010

---

### 测试10: 获取当前用户信息 - Token已过期

#### cURL 命令

```bash
# 使用过期的token
curl -X GET http://localhost:8080/api/v1/user/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.expired.token"
```

#### 预期响应

```json
{
  "code": 4010,
  "msg": "Token无效或已过期"
}
```

#### 验证点

- ✅ HTTP状态码 401
- ✅ code 为 4010

---

## Postman Collection 导入

可以创建以下Postman Collection：

```json
{
  "info": {
    "name": "戒了么服务端API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "1. 发送验证码",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"phone\": \"13800138000\",\n  \"scene\": \"login\"\n}"
        },
        "url": "{{baseUrl}}/api/v1/auth/sendCode"
      }
    },
    {
      "name": "2. 登录",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"phone\": \"13800138000\",\n  \"code\": \"123456\"\n}"
        },
        "url": "{{baseUrl}}/api/v1/auth/login"
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "var jsonData = pm.response.json();",
              "if (jsonData.code === 0) {",
              "  pm.collectionVariables.set('token', jsonData.data.token);",
              "}"
            ]
          }
        }
      ]
    },
    {
      "name": "3. 获取当前用户信息",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "{{token}}"
          }
        ],
        "url": "{{baseUrl}}/api/v1/user/me"
      }
    }
  ]
}
```

## 批量测试脚本

### Bash 脚本（test.sh）

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
PHONE="13900139999"

echo "========== 测试开始 =========="

echo "\n1. 测试发送验证码..."
SEND_CODE_RESP=$(curl -s -X POST $BASE_URL/api/v1/auth/sendCode \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"$PHONE\",\"scene\":\"login\"}")
echo "响应: $SEND_CODE_RESP"

echo "\n2. 测试登录..."
LOGIN_RESP=$(curl -s -X POST $BASE_URL/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"$PHONE\",\"code\":\"123456\"}")
echo "响应: $LOGIN_RESP"

# 提取token
TOKEN=$(echo $LOGIN_RESP | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Token: $TOKEN"

echo "\n3. 测试获取用户信息（不带token）..."
ME_NO_TOKEN=$(curl -s -X GET $BASE_URL/api/v1/user/me)
echo "响应: $ME_NO_TOKEN"

echo "\n4. 测试获取用户信息（带token）..."
ME_WITH_TOKEN=$(curl -s -X GET $BASE_URL/api/v1/user/me \
  -H "Authorization: $TOKEN")
echo "响应: $ME_WITH_TOKEN"

echo "\n========== 测试结束 =========="
```

### PowerShell 脚本（test.ps1）

```powershell
$baseUrl = "http://localhost:8080"
$phone = "13900139999"

Write-Host "========== 测试开始 ==========" -ForegroundColor Green

Write-Host "`n1. 测试发送验证码..." -ForegroundColor Yellow
$body = @{
    phone = $phone
    scene = "login"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/sendCode" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
Write-Host ($response | ConvertTo-Json)

Write-Host "`n2. 测试登录..." -ForegroundColor Yellow
$body = @{
    phone = $phone
    code = "123456"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
Write-Host ($response | ConvertTo-Json)
$token = $response.data.token

Write-Host "`n3. 测试获取用户信息（不带token）..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/user/me" -Method Get
    Write-Host ($response | ConvertTo-Json)
} catch {
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host "`n4. 测试获取用户信息（带token）..." -ForegroundColor Yellow
$headers = @{
    Authorization = $token
}
$response = Invoke-RestMethod -Uri "$baseUrl/api/v1/user/me" `
    -Method Get `
    -Headers $headers
Write-Host ($response | ConvertTo-Json)

Write-Host "`n========== 测试结束 ==========" -ForegroundColor Green
```

## 验收清单

### 功能验收

- [ ] 发送验证码成功
- [ ] 手机号格式校验正常
- [ ] 验证码频率限制生效（60秒/1小时）
- [ ] 新用户登录自动注册
- [ ] 昵称自动生成（格式正确）
- [ ] 老用户登录不重复创建
- [ ] last_login_at正确更新
- [ ] 验证码错误拦截
- [ ] 未授权访问被拦截
- [ ] Token鉴权正常
- [ ] Token格式错误被拦截
- [ ] Token过期被拦截
- [ ] 获取用户信息正常

### 数据库验收

- [ ] t_user表结构正确
- [ ] t_sms_code表结构正确
- [ ] 用户数据正确写入
- [ ] 验证码数据正确写入
- [ ] 索引创建正确

### 非功能验收

- [ ] 接口响应时间 < 500ms
- [ ] 日志输出正常
- [ ] 异常处理正常
- [ ] 跨域配置生效
- [ ] 数据库连接正常
- [ ] Maven编译无错误
