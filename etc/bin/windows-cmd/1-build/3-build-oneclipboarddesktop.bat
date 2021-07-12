@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :oneclipboarddesktop:compileJava
call gradlew.bat :oneclipboarddesktop:processResources
call gradlew.bat :oneclipboarddesktop:izPackCreateInstaller

%PRINT_SUCCESS_MESSAGE% && exit /b 0
