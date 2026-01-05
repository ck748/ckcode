@echo off
chcp 65001 >nul
echo ========================================
echo 修复管理员用户名显示问题
echo ========================================
echo.
echo 正在连接数据库并更新数据...
echo.

REM 请根据你的MySQL安装路径和数据库配置修改以下参数
set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
set DB_USER=root
set DB_NAME=defect_detection

REM 检查MySQL是否存在
if not exist "%MYSQL_PATH%" (
    echo [错误] 未找到MySQL，请手动执行SQL文件
    echo 请使用Navicat或MySQL Workbench打开并执行：
    echo d:\class\sql\update_manager_name.sql
    pause
    exit /b
)

REM 执行SQL更新
echo 请输入数据库密码：
"%MYSQL_PATH%" -u %DB_USER% -p %DB_NAME% < "d:\class\sql\update_manager_name.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo [成功] 数据库更新完成！
    echo ========================================
    echo.
    echo 下一步操作：
    echo 1. 按 F12 打开浏览器开发者工具
    echo 2. 切换到 Application 标签
    echo 3. 左侧找到 Local Storage
    echo 4. 删除 useradmin 这一项
    echo 5. 刷新页面重新登录
    echo.
) else (
    echo.
    echo [失败] 数据库更新失败，请手动执行
)

pause
