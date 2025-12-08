# 区块链功能快速启动脚本

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   区块链缺陷检测系统 - 快速启动" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 检查FISCO BCOS节点
Write-Host "🔍 检查FISCO BCOS节点..." -ForegroundColor Yellow
$nodeCheck = netstat -an | Select-String "5002"

if ($nodeCheck) {
    Write-Host "✅ FISCO BCOS节点正在运行 (端口:5002)" -ForegroundColor Green
} else {
    Write-Host "❌ FISCO BCOS节点未运行!" -ForegroundColor Red
    Write-Host "   请先启动FISCO BCOS节点,或确认节点端口是否为5002" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "按Enter继续(如果节点在其他端口运行)"
}

Write-Host ""
Write-Host "📦 项目配置信息:" -ForegroundColor Cyan
Write-Host "   - 项目路径: c:\Users\LENOVO\Desktop\v3q\DefectDetection\defectDetection" -ForegroundColor White
Write-Host "   - 区块链: 已启用 ✅" -ForegroundColor Green
Write-Host "   - 节点地址: 127.0.0.1:5002" -ForegroundColor White
Write-Host "   - 证书: 已配置 ✅" -ForegroundColor Green
Write-Host "   - 合约地址: 已配置 ✅" -ForegroundColor Green
Write-Host ""

Write-Host "🚀 准备启动项目..." -ForegroundColor Yellow
Write-Host ""

# 进入项目目录
Set-Location "c:\Users\LENOVO\Desktop\v3q\DefectDetection\defectDetection"

Write-Host "📝 启动Spring Boot应用..." -ForegroundColor Cyan
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 启动项目
mvn spring-boot:run

# 如果项目停止,显示信息
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   项目已停止" -ForegroundColor Yellow
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📚 接下来你可以:" -ForegroundColor Cyan
Write-Host "   1. 访问 http://localhost:8081/swagger-ui/index.html 测试API" -ForegroundColor White
Write-Host "   2. 测试区块链状态: GET /blockchain/status" -ForegroundColor White
Write-Host "   3. 测试原材料合约: POST /blockchain/rawMaterial/test" -ForegroundColor White
Write-Host ""
Write-Host "📖 查看文档: 区块链使用指南.md" -ForegroundColor Green
Write-Host ""

Read-Host "按Enter键退出"
