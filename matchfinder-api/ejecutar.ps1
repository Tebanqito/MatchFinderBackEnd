# Uso (desde esta carpeta):  powershell -ExecutionPolicy Bypass -File .\ejecutar.ps1
$ErrorActionPreference = "Stop"

# 1. Usar el JDK que responde a "java" (tiene que ser el 25)
$jdk = (java -XshowSettings:properties -version 2>&1 | Select-String "java.home").ToString().Split("=")[1].Trim()
$env:JAVA_HOME = $jdk
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Write-Host "Usando Java en: $env:JAVA_HOME"

function Nueva-Clave($bytes) {
    $b = New-Object byte[] $bytes
    [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b)
    [Convert]::ToBase64String($b)
}

function Escapar($texto) { $texto.Replace("'", "''") }

# 2. Primera vez: pide los datos de la base y genera las claves. Se guarda en .env.local.ps1
$archivo = Join-Path $PSScriptRoot ".env.local.ps1"
if (-not (Test-Path $archivo)) {
    $dbUrl = Read-Host "URL de la base (Enter para jdbc:postgresql://localhost:5432/matchfinder)"
    if (-not $dbUrl) { $dbUrl = "jdbc:postgresql://localhost:5432/matchfinder" }
    $dbUser = Read-Host "Usuario de PostgreSQL (Enter para postgres)"
    if (-not $dbUser) { $dbUser = "postgres" }
    $dbPass = Read-Host "Contraseña de PostgreSQL"

    @(
        "`$env:DB_URL = '$(Escapar $dbUrl)'",
        "`$env:DB_USER = '$(Escapar $dbUser)'",
        "`$env:DB_PASSWORD = '$(Escapar $dbPass)'",
        "`$env:JWT_SECRET = '$(Nueva-Clave 32)'",
        "`$env:OWNER_REGISTRATION_KEY = '$(Nueva-Clave 48)'"
    ) | Set-Content $archivo -Encoding UTF8
    Write-Host "Configuración guardada en .env.local.ps1"
}

# 3. Cargar la configuración y arrancar
. $archivo
Write-Host ""
Write-Host "Clave para dar de alta al owner (header X-Owner-Key):"
Write-Host $env:OWNER_REGISTRATION_KEY
Write-Host ""

mvn spring-boot:run
