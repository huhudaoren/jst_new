# =====================================================================
# 文件名：restart-admin.ps1
# 用途  : 一键 kill 8080 + 重新启动 ruoyi-admin (避免端口冲突)
# 用法  : 在 PowerShell 中执行 → 等待启动完成
#         pwsh D:\coding\jst_v1\scripts\restart-admin.ps1
# =====================================================================

Write-Host "===== 1. 检查 8080 端口占用 =====" -ForegroundColor Cyan
$conns = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($conns) {
    Write-Host "发现占用进程,正在 kill..." -ForegroundColor Yellow
    $conns | ForEach-Object {
        $pid = $_.OwningProcess
        try {
            Stop-Process -Id $pid -Force -ErrorAction Stop
            Write-Host "  killed PID $pid" -ForegroundColor Green
        } catch {
            Write-Host "  失败 PID $pid : $_" -ForegroundColor Red
        }
    }
    Start-Sleep -Seconds 2
} else {
    Write-Host "8080 已空闲" -ForegroundColor Green
}

Write-Host ""
Write-Host "===== 2. 二次确认 8080 已释放 =====" -ForegroundColor Cyan
$check = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($check) {
    Write-Host "❌ 8080 仍被占用,放弃" -ForegroundColor Red
    exit 1
} else {
    Write-Host "✅ 8080 已空闲" -ForegroundColor Green
}

Write-Host ""
Write-Host "===== 3. 启动 ruoyi-admin (前台运行,Ctrl+C 停止) =====" -ForegroundColor Cyan
Write-Host "等待 'Started RuoYiApplication' 出现后,在 IDEA 跑 test/api-tests.http"
Write-Host ""

Set-Location "D:\coding\jst_v1\RuoYi-Vue"
mvn -pl ruoyi-admin -am spring-boot:run -DskipTests
