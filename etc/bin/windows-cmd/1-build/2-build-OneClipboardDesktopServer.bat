@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :OneClipboardDesktopServer:compileJava
call gradlew.bat :OneClipboardDesktopServer:processResources
call gradlew.bat :OneClipboardDesktopServer:izPackCreateInstaller

%PRINT_SUCCESS_MESSAGE% && exit /b 0
