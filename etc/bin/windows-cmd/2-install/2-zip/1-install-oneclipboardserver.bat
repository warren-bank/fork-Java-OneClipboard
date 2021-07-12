@echo off

call "%~dp0..\..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

set INSTALLER_ZIP=OneClipboardServer\build\distributions\OneClipboardServer-%ONECLIPBOARD_VERSION%.zip

rem :: skip if installer zip was not generated by build script
if not exist "%INSTALLER_ZIP%" %PRINT_FAILURE_MESSAGE% "ERROR: No installer zip generated by build" && exit /b 1

rem :: skip if destination directory is invalid
if not defined INSTALLER_ZIP_BASE_DIR   %PRINT_FAILURE_MESSAGE% "ERROR: Installation directory is not defined" && exit /b 1
if not exist "%INSTALLER_ZIP_BASE_DIR%" %PRINT_FAILURE_MESSAGE% "ERROR: Installation directory does not exist" && exit /b 1

set INSTALLER_ZIP_DIR=%INSTALLER_ZIP_BASE_DIR%\OneClipboardServer

rem :: skip if destination directory already exists
if exist "%INSTALLER_ZIP_DIR%" %PRINT_FAILURE_MESSAGE% "ERROR: Installation directory already exists" && exit /b 1

mkdir "%INSTALLER_ZIP_DIR%"
7z x "%INSTALLER_ZIP%" -r -o"%INSTALLER_ZIP_DIR%"

%PRINT_SUCCESS_MESSAGE% && exit /b 0
