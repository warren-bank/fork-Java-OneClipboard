@echo off

call "%~dp0..\..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

set INSTALLER_JAR=oneclipboarddesktop\build\distributions\oneclipboarddesktop-1.03.00-installer.jar

rem :: skip if installer jar was not generated by build script
if not exist "%INSTALLER_JAR%" %PRINT_FAILURE_MESSAGE% "ERROR: No installer jar generated by build" && exit /b 1

java -jar "%INSTALLER_JAR%"

%PRINT_SUCCESS_MESSAGE% && exit /b 0
