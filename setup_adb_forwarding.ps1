# =====================================================================
# TruthLens - Android Phone to XAMPP Connection Setup & Verification
# =====================================================================

$ErrorActionPreference = "Continue"

Write-Host "`n=====================================================================" -ForegroundColor Cyan
Write-Host "       TruthLens - Android Phone to XAMPP Connection Helper" -ForegroundColor Cyan
Write-Host "=====================================================================`n" -ForegroundColor Cyan

# 1. Locate ADB
$adbCmd = Get-Command adb.exe -ErrorAction SilentlyContinue
$adbPath = if ($adbCmd) { $adbCmd.Source } else { $null }

if (-not $adbPath) {
    $candidates = @(
        "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
        "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools\adb.exe",
        "C:\Program Files\Android\platform-tools\adb.exe"
    )
    foreach ($cand in $candidates) {
        if (Test-Path $cand) {
            $adbPath = $cand
            break
        }
    }
}

if (-not $adbPath) {
    Write-Host "[ERROR] adb.exe could not be found." -ForegroundColor Red
    Write-Host "Please ensure Android SDK Platform-Tools is installed." -ForegroundColor Yellow
    exit 1
}

Write-Host "[OK] ADB path: $adbPath" -ForegroundColor Green

# 2. Check Apache Status
Write-Host "`n[1/4] Checking XAMPP Apache listening port..." -ForegroundColor Yellow
$apacheConn = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object { $_.LocalPort -eq 80 }
if (-not $apacheConn) {
    Write-Host "[WARNING] Port 80 does not appear to be active." -ForegroundColor Yellow
    Write-Host "Please ensure Apache is started in the XAMPP Control Panel." -ForegroundColor Yellow
} else {
    Write-Host "[OK] Apache is running and listening on port 80." -ForegroundColor Green
}

# 3. Check Connected Devices
Write-Host "`n[2/4] Checking connected Android devices..." -ForegroundColor Yellow
& "$adbPath" start-server | Out-Null
$devicesOutput = & "$adbPath" devices
$deviceLines = $devicesOutput | Where-Object { $_ -match "\bdevice\b" -and $_ -notmatch "List of devices" }
$unauthorized = $devicesOutput | Where-Object { $_ -match "\bunauthorized\b" }

if ($unauthorized) {
    Write-Host "[ACTION REQUIRED] Phone detected but UNAUTHORIZED!" -ForegroundColor Magenta
    Write-Host "Unlock your phone screen and tap 'Allow USB debugging' (check 'Always allow')." -ForegroundColor Yellow
    exit 1
}

if (-not $deviceLines) {
    Write-Host "[ERROR] No authorized Android device connected over USB." -ForegroundColor Red
    Write-Host "Please:" -ForegroundColor Yellow
    Write-Host "  1. Connect your phone via USB." -ForegroundColor Yellow
    Write-Host "  2. Enable Settings -> Developer Options -> USB Debugging." -ForegroundColor Yellow
    exit 1
}

$deviceId = ($deviceLines[0] -split "`t")[0]
Write-Host "[OK] Connected device: $deviceId" -ForegroundColor Green

# 4. Set ADB Reverse Port Forwarding
Write-Host "`n[3/4] Setting ADB reverse forwarding: tcp:80 -> tcp:80..." -ForegroundColor Yellow
& "$adbPath" reverse tcp:80 tcp:80
$activeRules = & "$adbPath" reverse --list
Write-Host "[OK] Active rules:`n$activeRules" -ForegroundColor Green

# 5. Live Test End-to-End
Write-Host "`n[4/4] Testing backend connectivity directly from the phone..." -ForegroundColor Yellow
$httpTest = & "$adbPath" shell "curl -s -i http://127.0.0.1/Truthlense_backened/setup_database.php"
if ($httpTest -match "HTTP/\S+\s+200") {
    Write-Host "[SUCCESS] Phone reached XAMPP Apache with HTTP 200 OK!" -ForegroundColor Green
    Write-Host "`n=====================================================================" -ForegroundColor Cyan
    Write-Host "TruthLens backend is ready. You can now use Login, Sign Up, and OTP!" -ForegroundColor Cyan
    Write-Host "=====================================================================`n" -ForegroundColor Cyan
} else {
    Write-Host "[NOTICE] HTTP test output:`n$httpTest" -ForegroundColor Yellow
}
