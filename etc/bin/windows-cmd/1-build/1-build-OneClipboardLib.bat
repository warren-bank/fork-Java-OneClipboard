@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :OneClipboardLibCommon:compileJava
call gradlew.bat :OneClipboardLibClient:compileJava
call gradlew.bat :OneClipboardLibServer:compileJava
call gradlew.bat :OneClipboardLibDesktop:compileJava

%PRINT_SUCCESS_MESSAGE% && exit /b 0
