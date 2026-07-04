@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "DB_URL=%DB_URL%"
set "DB_USER=%DB_USER%"
set "DB_PASS=%DB_PASS%"
cd /d C:\myproject\dress\backend
start /B java.exe -jar target\learn-1.0.0.jar > backend_runtime.log 2>&1
exit
