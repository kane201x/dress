@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "DB_URL=jdbc:mysql://YOUR_SERVER_IP:3306/knowall?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
set "DB_USER=root"
set "DB_PASS=YOUR_DB_PASS"
cd /d C:\myproject\dress\backend
start /B java.exe -jar target\learn-1.0.0.jar > backend_runtime.log 2>&1
exit
