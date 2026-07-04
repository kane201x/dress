@echo off
cd /d "%~dp0frontend"
echo === Vite build ===
cmd /c npm run build
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo === Copy dist to android assets ===
xcopy /y /s /e /i "dist\*" "..\android\app\src\main\assets\"

echo === Gradle assembleDebug ===
cd /d "%~dp0android"
cmd /c C:\Android\gradle\gradle-8.2\bin\gradle.bat assembleDebug
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo === Done ===
echo APK: android\app\build\outputs\apk\debug\app-debug.apk
