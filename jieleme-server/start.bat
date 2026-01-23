@echo off
chcp 65001 > nul
echo ========================================
echo   戒了么服务端启动脚本
echo ========================================
echo.

echo [1/3] 检查环境...
echo.

:: 检查 Java
java -version > nul 2>&1
if errorlevel 1 (
    echo ❌ 未检测到 Java，请先安装 JDK 17
    pause
    exit /b 1
)
echo ✅ Java 已安装

:: 检查 Maven
mvn -version > nul 2>&1
if errorlevel 1 (
    echo ❌ 未检测到 Maven，请先安装 Maven
    pause
    exit /b 1
)
echo ✅ Maven 已安装

echo.
echo [2/3] 准备启动...
echo.
echo 📝 重要提示：
echo    1. 请确保 MySQL 服务已启动
echo    2. 请确保已创建数据库和表（执行 init-database.sql）
echo    3. 请确保 application.yml 中的密码正确
echo.
echo 按任意键继续启动，或按 Ctrl+C 取消...
pause > nul

echo.
echo [3/3] 正在启动服务...
echo.

:: 启动 Spring Boot
mvn spring-boot:run

pause
