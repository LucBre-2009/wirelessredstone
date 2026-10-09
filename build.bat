@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title WirelessRedstone - Build

echo ==========================================
echo   WirelessRedstone - automatic build
echo ==========================================
echo.

where java >nul 2>nul
if errorlevel 1 (
  echo ERROR: Java was not found.
  echo Install a Java 25 JDK, then run this file again.
  echo.
  pause
  exit /b 1
)

for /f "tokens=3" %%V in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VER=%%~V
if not defined JAVA_VER (
  echo ERROR: Could not detect the installed Java version.
  java -version
  pause
  exit /b 1
)

echo Detected Java: %JAVA_VER%
echo Checking for Java 25 or newer...
for /f "tokens=1 delims=." %%M in ("%JAVA_VER%") do set "JAVA_MAJOR=%%M"
if not defined JAVA_MAJOR (
  echo ERROR: Could not parse the Java version.
  pause
  exit /b 1
)
if %JAVA_MAJOR% LSS 25 (
  echo ERROR: Minecraft 26.3 mod builds require Java 25 or newer.
  echo Install a Java 25 JDK and make sure 'java' points to it.
  pause
  exit /b 1
)

set "TOOLS=%CD%\.tools"
set "GRADLE_VERSION=9.6.0"
set "GRADLE_HOME_LOCAL=%TOOLS%\gradle-%GRADLE_VERSION%"
set "GRADLE_ZIP=%TOOLS%\gradle-%GRADLE_VERSION%-bin.zip"
if not exist "%TOOLS%" mkdir "%TOOLS%"

if not exist "%GRADLE_HOME_LOCAL%\bin\gradle.bat" (
  echo Gradle %GRADLE_VERSION% is missing. Downloading it now...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_ZIP%'; Expand-Archive -LiteralPath '%GRADLE_ZIP%' -DestinationPath '%TOOLS%' -Force"
  if errorlevel 1 (
    echo ERROR: Could not download Gradle. Check your internet connection.
    pause
    exit /b 1
  )
)

echo.
echo Building the mod. The first build downloads Minecraft and Fabric dependencies.
echo This may take several minutes.
echo.
call "%GRADLE_HOME_LOCAL%\bin\gradle.bat" --no-daemon clean build
if errorlevel 1 (
  echo.
  echo BUILD FAILED. The JAR was not marked as successful.
  echo Check the error output above. Verify that Minecraft 26.3 dependencies are available.
  pause
  exit /b 1
)

if not exist "%CD%\output" mkdir "%CD%\output"
for %%F in ("%CD%\build\libs\*.jar") do (
  echo %%~nxF | findstr /i "sources" >nul
  if errorlevel 1 (
    echo %%~nxF | findstr /i "-dev" >nul
    if errorlevel 1 copy /y "%%~fF" "%CD%\output\" >nul
  )
)

echo.
echo BUILD SUCCEEDED. Check the output folder for the mod JAR.
echo Note: this scaffold currently registers the two blocks only.
echo Wireless frequency logic and the configuration menu are not implemented yet.
echo.
pause
