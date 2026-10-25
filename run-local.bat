@echo off
rem AgroGest - compilar com Maven, publicar no Tomcat local e arrancar (Windows)
rem Requisitos: JDK 17 em C:\tools\jdk17, Maven em C:\tools\maven, Tomcat 10.1 em C:\tools\tomcat10

setlocal
if "%JAVA_HOME%"=="" set "JAVA_HOME=C:\tools\jdk17"
if "%CATALINA_HOME%"=="" set "CATALINA_HOME=C:\tools\tomcat10"
set "PATH=%JAVA_HOME%\bin;C:\tools\maven\bin;%PATH%"

cd /d "%~dp0"

echo [1/4] A compilar com Maven...
call mvn -q clean package
if errorlevel 1 (
    echo ERRO: a compilacao falhou.
    exit /b 1
)

echo [2/4] A parar o Tomcat (se estiver a correr; ao parar o estado e gravado)...
call "%CATALINA_HOME%\bin\shutdown.bat" >nul 2>&1
timeout /t 5 /nobreak >nul

echo [3/4] A publicar target\agrogest.war...
if exist "%CATALINA_HOME%\webapps\agrogest" rmdir /s /q "%CATALINA_HOME%\webapps\agrogest"
copy /y "target\agrogest.war" "%CATALINA_HOME%\webapps\agrogest.war" >nul

echo [4/4] A arrancar o Tomcat...
call "%CATALINA_HOME%\bin\startup.bat"

echo.
echo Abrir no navegador: http://localhost:8081/agrogest
endlocal
