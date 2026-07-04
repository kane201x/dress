@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
set "MAVEN_HOME=C:\Tools\apache-maven-3.9.9"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"
set "DB_URL=%DB_URL%"
set "DB_USER=%DB_USER%"
set "DB_PASS=%DB_PASS%"
cd /d C:\myproject\dress\backend
"C:\Tools\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run > C:\myproject\dress\backend.log 2>&1
