$env:DB_URL = $env:DB_URL
$env:DB_USER = $env:DB_USER
$env:DB_PASS = $env:DB_PASS

Set-Location "C:\myproject\dress\backend"
& "mvn.cmd" spring-boot:run 2>&1 | ForEach-Object { $_ }
