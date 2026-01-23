# 戒了么服务端启动脚本 (PowerShell)

Write-Host "========================================" -ForegroundColor Green
Write-Host "  戒了么服务端启动脚本" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "[1/3] 检查环境..." -ForegroundColor Yellow
Write-Host ""

# 检查 Java
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✅ Java 已安装: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ 未检测到 Java，请先安装 JDK 17" -ForegroundColor Red
    exit 1
}

# 检查 Maven
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "✅ Maven 已安装: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ 未检测到 Maven，请先安装 Maven" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] 准备启动..." -ForegroundColor Yellow
Write-Host ""
Write-Host "📝 重要提示：" -ForegroundColor Cyan
Write-Host "   1. 请确保 MySQL 服务已启动"
Write-Host "   2. 请确保已创建数据库和表（执行 init-database.sql）"
Write-Host "   3. 请确保 application.yml 中的密码正确"
Write-Host ""
Write-Host "按回车键继续启动，或按 Ctrl+C 取消..." -ForegroundColor Yellow
Read-Host

Write-Host ""
Write-Host "[3/3] 正在启动服务..." -ForegroundColor Yellow
Write-Host ""

# 启动 Spring Boot
mvn spring-boot:run
