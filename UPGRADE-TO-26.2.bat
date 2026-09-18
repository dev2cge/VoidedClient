@echo off
setlocal
set "BOOT=%TEMP%\voidedclient-gradle-951"
if exist "%BOOT%" rmdir /s /q "%BOOT%"
mkdir "%BOOT%"
pushd "%BOOT%"
>settings.gradle echo rootProject.name = 'wrapper-bootstrap'
>build.gradle echo // isolated wrapper bootstrap - intentionally empty
gradle wrapper --gradle-version 9.5.1 --distribution-type bin
if errorlevel 1 goto :fail
popd
if not exist "gradle\wrapper" mkdir "gradle\wrapper"
copy /Y "%BOOT%\gradlew" "gradlew" >nul
copy /Y "%BOOT%\gradlew.bat" "gradlew.bat" >nul
copy /Y "%BOOT%\gradle\wrapper\gradle-wrapper.jar" "gradle\wrapper\gradle-wrapper.jar" >nul
copy /Y "%BOOT%\gradle\wrapper\gradle-wrapper.properties" "gradle\wrapper\gradle-wrapper.properties" >nul
echo.
echo VoidedClient wrapper installed: Gradle 9.5.1
echo Set JAVA_HOME / IntelliJ Gradle JVM to JDK 25, then run:
echo   gradlew --version
echo   gradlew :fabric:clean :fabric:build
exit /b 0
:fail
popd
echo Failed to bootstrap Gradle 9.5.1. Make sure the global 'gradle' command is available.
exit /b 1
