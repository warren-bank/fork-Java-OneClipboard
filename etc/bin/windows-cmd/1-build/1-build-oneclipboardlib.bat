@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :oneclipboardlib:compileJava

%PRINT_SUCCESS_MESSAGE% && exit /b 0
