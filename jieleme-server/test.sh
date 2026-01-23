#!/bin/bash
# 戒了么服务端 API 测试脚本 (Bash)
# 使用方法: chmod +x test.sh && ./test.sh

BASE_URL="http://localhost:8080"
PHONE="13900139999"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

function print_header() {
    echo -e "\n${GREEN}========== $1 ==========${NC}"
}

function print_step() {
    echo -e "\n${YELLOW}[$1]${NC}"
}

function print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

function print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_header "API 测试开始"

# 测试1: 发送验证码
print_step "测试1: 发送验证码"
RESPONSE=$(curl -s -X POST $BASE_URL/api/v1/auth/sendCode \
    -H "Content-Type: application/json" \
    -d "{\"phone\":\"$PHONE\",\"scene\":\"login\"}")

CODE=$(echo $RESPONSE | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$CODE" == "0" ]; then
    print_success "发送成功"
    REQUEST_ID=$(echo $RESPONSE | grep -o '"requestId":"[^"]*"' | cut -d'"' -f4)
    echo "  RequestId: $REQUEST_ID"
else
    print_error "发送失败: $RESPONSE"
fi

# 等待1秒
sleep 1

# 测试2: 登录（自动注册）
print_step "测试2: 登录（新用户自动注册）"
RESPONSE=$(curl -s -X POST $BASE_URL/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d "{\"phone\":\"$PHONE\",\"code\":\"123456\"}")

CODE=$(echo $RESPONSE | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$CODE" == "0" ]; then
    print_success "登录成功"
    TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    NICKNAME=$(echo $RESPONSE | grep -o '"nickname":"[^"]*"' | cut -d'"' -f4)
    USER_ID=$(echo $RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    echo "  用户ID: $USER_ID"
    echo "  手机号: $PHONE"
    echo "  昵称: $NICKNAME"
    echo "  Token: ${TOKEN:0:30}..."
else
    print_error "登录失败: $RESPONSE"
fi

# 测试3: 获取用户信息（不带token）
print_step "测试3: 获取用户信息（未授权）"
RESPONSE=$(curl -s -w "\n%{http_code}" -X GET $BASE_URL/api/v1/user/me)
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)

if [ "$HTTP_CODE" == "401" ]; then
    print_success "正确拦截未授权请求"
else
    print_error "应该返回401错误，但返回了$HTTP_CODE"
fi

# 测试4: 获取用户信息（带token）
print_step "测试4: 获取用户信息（已授权）"
RESPONSE=$(curl -s -X GET $BASE_URL/api/v1/user/me \
    -H "Authorization: $TOKEN")

CODE=$(echo $RESPONSE | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$CODE" == "0" ]; then
    print_success "获取成功"
    NICKNAME=$(echo $RESPONSE | grep -o '"nickname":"[^"]*"' | cut -d'"' -f4)
    echo "  昵称: $NICKNAME"
else
    print_error "获取失败: $RESPONSE"
fi

# 测试5: 验证码频率限制
print_step "测试5: 验证码频率限制"
RESPONSE=$(curl -s -X POST $BASE_URL/api/v1/auth/sendCode \
    -H "Content-Type: application/json" \
    -d "{\"phone\":\"$PHONE\",\"scene\":\"login\"}")

CODE=$(echo $RESPONSE | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$CODE" == "1004" ]; then
    print_success "频率限制生效"
    MSG=$(echo $RESPONSE | grep -o '"msg":"[^"]*"' | cut -d'"' -f4)
    echo "  错误信息: $MSG"
else
    print_error "应该触发频率限制，但返回了code=$CODE"
fi

# 测试6: 验证码错误
print_step "测试6: 验证码错误"
TEST_PHONE="13800138888"
RESPONSE=$(curl -s -X POST $BASE_URL/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d "{\"phone\":\"$TEST_PHONE\",\"code\":\"999999\"}")

CODE=$(echo $RESPONSE | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$CODE" == "1002" ]; then
    print_success "验证码校验生效"
    MSG=$(echo $RESPONSE | grep -o '"msg":"[^"]*"' | cut -d'"' -f4)
    echo "  错误信息: $MSG"
else
    print_error "应该返回验证码错误，但返回了code=$CODE"
fi

# 测试7: 手机号格式错误
print_step "测试7: 手机号格式错误"
RESPONSE=$(curl -s -X POST $BASE_URL/api/v1/auth/sendCode \
    -H "Content-Type: application/json" \
    -d "{\"phone\":\"1234567890\",\"scene\":\"login\"}")

CODE=$(echo $RESPONSE | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$CODE" == "1001" ]; then
    print_success "手机号格式校验生效"
    MSG=$(echo $RESPONSE | grep -o '"msg":"[^"]*"' | cut -d'"' -f4)
    echo "  错误信息: $MSG"
else
    print_error "应该返回格式错误，但返回了code=$CODE"
fi

print_header "测试完成"
echo -e "\n${CYAN}测试总结:${NC}"
echo "  - 发送验证码"
echo "  - 登录（自动注册）"
echo "  - 鉴权拦截"
echo "  - 获取用户信息"
echo "  - 频率限制"
echo "  - 验证码校验"
echo "  - 格式校验"
echo ""
