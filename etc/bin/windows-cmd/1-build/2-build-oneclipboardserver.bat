@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :OneClipboardServer:compileJava
call gradlew.bat :OneClipboardServer:processResources
call gradlew.bat :OneClipboardServer:izPackCreateInstaller

%PRINT_SUCCESS_MESSAGE% && exit /b 0
