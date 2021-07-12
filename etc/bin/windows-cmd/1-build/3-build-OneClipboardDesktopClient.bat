@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :OneClipboardDesktopClient:compileJava
call gradlew.bat :OneClipboardDesktopClient:processResources
call gradlew.bat :OneClipboardDesktopClient:izPackCreateInstaller

%PRINT_SUCCESS_MESSAGE% && exit /b 0
