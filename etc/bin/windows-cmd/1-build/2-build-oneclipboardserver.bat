@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :oneclipboardserver:compileJava
call gradlew.bat :oneclipboardserver:processResources
call gradlew.bat :oneclipboardserver:izPackCreateInstaller

%PRINT_SUCCESS_MESSAGE% && exit /b 0
