@echo off
setlocal EnableDelayedExpansion

title "TruthLens ADB Port Forwarding and Connection Helper"

echo =====================================================================
echo           TruthLens - Android Phone to XAMPP Connection
echo =====================================================================
echo.

:: 1. Locate ADB
set "ADB_EXE="
where adb.exe >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set "ADB_EXE=adb.exe"
) else (
    if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
        set "ADB_EXE=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    ) else if exist "C:\Users\SCAD LAB\AppData\Local\Android\Sdk\platform-tools\adb.exe" (
        set "ADB_EXE=C:\Users\SCAD LAB\AppData\Local\Android\Sdk\platform-tools\adb.exe"
    )
)

if "%ADB_EXE%"=="" (
    echo [ERROR] ADB executable not found.
    echo Please install Android SDK Platform Tools.
    goto :end
)

echo [OK] Using ADB: "%ADB_EXE%"
echo.

:: 2. Check Apache Status
echo [1/4] Checking XAMPP Apache status (Port 80)...
netstat -ano | findstr ":80" | findstr "LISTENING" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [WARNING] Port 80 does not appear to be active.
    echo Please ensure XAMPP Apache is started in XAMPP Control Panel.
    echo.
) else (
    echo [OK] Apache is active and listening on port 80.
    echo.
)

:: 3. Check Connected Devices
echo [2/4] Checking connected Android devices...
"%ADB_EXE%" start-server >nul 2>&1

set "DEVICE_COUNT=0"
"%ADB_EXE%" devices > "%TEMP%\truthlens_devices.txt" 2>&1

for /f "skip=1 tokens=1,2" %%A in ('type "%TEMP%\truthlens_devices.txt"') do (
    if "%%B"=="device" (
        set /a DEVICE_COUNT+=1
        set "LAST_DEVICE=%%A"
    )
    if "%%B"=="unauthorized" (
        echo [ACTION REQUIRED] Phone detected but UNAUTHORIZED.
        echo Unlock your phone and tap 'Allow USB debugging' ^(check 'Always allow'^).
        del "%TEMP%\truthlens_devices.txt" >nul 2>&1
        goto :end
    )
)
del "%TEMP%\truthlens_devices.txt" >nul 2>&1

if %DEVICE_COUNT% equ 0 (
    echo [ERROR] No authorized Android device connected over USB.
    echo Steps to fix:
    echo   1. Connect phone to PC using USB cable.
    echo   2. On phone, enable Settings -^> Developer Options -^> USB Debugging.
    echo   3. Set USB mode to 'File Transfer' if prompted.
    echo.
    goto :end
)

echo [OK] Detected authorized Android device: !LAST_DEVICE!
echo.

:: 4. Set ADB Reverse Port Forwarding
echo [3/4] Configuring ADB reverse forwarding (tcp:80 -^> tcp:80)...
"%ADB_EXE%" reverse tcp:80 tcp:80
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to execute adb reverse tcp:80 tcp:80.
    goto :end
)
echo [OK] Active reverse rules:
"%ADB_EXE%" reverse --list
echo.

:: 5. Test Live Phone-to-PC Connectivity
echo [4/4] Verifying connectivity directly from phone...
"%ADB_EXE%" shell "curl -s -o /dev/null -w '%%{http_code}' http://127.0.0.1/Truthlense_backened/setup_database.php" > "%TEMP%\truthlens_code.txt" 2>nul
set /p TEST_HTTP_CODE=<"%TEMP%\truthlens_code.txt"
del "%TEMP%\truthlens_code.txt" >nul 2>&1

if "!TEST_HTTP_CODE!"=="200" (
    echo [SUCCESS] Phone reached XAMPP Apache successfully with HTTP 200 OK!
    echo.
    echo =====================================================================
    echo TruthLens connection is verified! You can now use Login, Sign Up, OTP.
    echo =====================================================================
) else (
    echo [INFO] Response code: [!TEST_HTTP_CODE!]
    echo Forwarding is active. Ensure Apache responded to setup_database.php.
)

:end
echo.
echo Press any key to exit...
pause >nul
