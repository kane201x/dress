$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
$env:MAVEN_HOME = "C:\Tools\apache-maven-3.9.9"
$env:Path = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;C:\Program Files\nodejs;$env:Path"
$env:DB_URL = "jdbc:mysql://YOUR_SERVER_IP:3306/knowall?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASS = "YOUR_DB_PASS"

Set-Location "C:\myproject\dress\backend"
& "mvn.cmd" spring-boot:run 2>&1 | ForEach-Object { $_ }
