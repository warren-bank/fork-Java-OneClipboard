@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :OneClipboardDesktop:compileJava
call gradlew.bat :OneClipboardDesktop:processResources
call gradlew.bat :OneClipboardDesktop:izPackCreateInstaller

%PRINT_SUCCESS_MESSAGE% && exit /b 0
