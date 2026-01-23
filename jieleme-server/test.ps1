# 戒了么服务端 API 测试脚本 (PowerShell)
# 使用方法: .\test.ps1

$baseUrl = "http://localhost:8080"
$phone = "13900139999"

function Write-TestHeader {
    param([string]$text)
    Write-Host "`n========== $text ==========" -ForegroundColor Green
}

function Write-TestStep {
    param([string]$text)
    Write-Host "`n[$text]" -ForegroundColor Yellow
}

Write-TestHeader "API 测试开始"

# 测试1: 发送验证码
Write-TestStep "测试1: 发送验证码"
try {
    $body = @{
        phone = $phone
        scene = "login"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/sendCode" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "✓ 发送成功" -ForegroundColor Green
    Write-Host "  RequestId: $($response.data.requestId)"
    Write-Host "  过期时间: $($response.data.expireSeconds)秒"
} catch {
    Write-Host "✗ 发送失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 等待1秒
Start-Sleep -Seconds 1

# 测试2: 登录（自动注册）
Write-TestStep "测试2: 登录（新用户自动注册）"
try {
    $body = @{
        phone = $phone
        code = "123456"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "✓ 登录成功" -ForegroundColor Green
    Write-Host "  用户ID: $($response.data.user.id)"
    Write-Host "  手机号: $($response.data.user.phone)"
    Write-Host "  昵称: $($response.data.user.nickname)"
    Write-Host "  Token: $($response.data.token.Substring(0, 30))..."
    
    $global:token = $response.data.token
} catch {
    Write-Host "✗ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试3: 获取用户信息（不带token）
Write-TestStep "测试3: 获取用户信息（未授权）"
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/user/me" `
        -Method Get `
        -ErrorAction Stop
    
    Write-Host "✗ 应该返回401错误，但成功了" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "✓ 正确拦截未授权请求" -ForegroundColor Green
    } else {
        Write-Host "✗ 错误: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试4: 获取用户信息（带token）
Write-TestStep "测试4: 获取用户信息（已授权）"
try {
    $headers = @{
        Authorization = $global:token
    }
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/user/me" `
        -Method Get `
        -Headers $headers
    
    Write-Host "✓ 获取成功" -ForegroundColor Green
    Write-Host "  用户ID: $($response.data.id)"
    Write-Host "  手机号: $($response.data.phone)"
    Write-Host "  昵称: $($response.data.nickname)"
} catch {
    Write-Host "✗ 获取失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试5: 验证码频率限制
Write-TestStep "测试5: 验证码频率限制"
try {
    $body = @{
        phone = $phone
        scene = "login"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/sendCode" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body `
        -ErrorAction Stop
    
    Write-Host "✗ 应该触发频率限制，但成功了" -ForegroundColor Red
} catch {
    $errorBody = $_.ErrorDetails.Message | ConvertFrom-Json
    if ($errorBody.code -eq 1004) {
        Write-Host "✓ 频率限制生效" -ForegroundColor Green
        Write-Host "  错误信息: $($errorBody.msg)"
    } else {
        Write-Host "✗ 错误: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试6: 验证码错误
Write-TestStep "测试6: 验证码错误"
try {
    $testPhone = "13800138888"
    $body = @{
        phone = $testPhone
        code = "999999"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body `
        -ErrorAction Stop
    
    Write-Host "✗ 应该返回验证码错误，但成功了" -ForegroundColor Red
} catch {
    $errorBody = $_.ErrorDetails.Message | ConvertFrom-Json
    if ($errorBody.code -eq 1002) {
        Write-Host "✓ 验证码校验生效" -ForegroundColor Green
        Write-Host "  错误信息: $($errorBody.msg)"
    } else {
        Write-Host "✗ 错误: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试7: 手机号格式错误
Write-TestStep "测试7: 手机号格式错误"
try {
    $body = @{
        phone = "1234567890"
        scene = "login"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/sendCode" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body `
        -ErrorAction Stop
    
    Write-Host "✗ 应该返回格式错误，但成功了" -ForegroundColor Red
} catch {
    $errorBody = $_.ErrorDetails.Message | ConvertFrom-Json
    if ($errorBody.code -eq 1001) {
        Write-Host "✓ 手机号格式校验生效" -ForegroundColor Green
        Write-Host "  错误信息: $($errorBody.msg)"
    } else {
        Write-Host "✗ 错误: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-TestHeader "测试完成"
Write-Host "`n测试总结:" -ForegroundColor Cyan
Write-Host "  - 发送验证码"
Write-Host "  - 登录（自动注册）"
Write-Host "  - 鉴权拦截"
Write-Host "  - 获取用户信息"
Write-Host "  - 频率限制"
Write-Host "  - 验证码校验"
Write-Host "  - 格式校验"
Write-Host ""
