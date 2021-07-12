@echo off

call "%~dp0..\0-shared\env.bat"

rem :: skip if destination directory is invalid
if not defined INSTALLER_ZIP_BASE_DIR   %PRINT_FAILURE_MESSAGE% "ERROR: Installation directory is not defined" && exit /b 1
if not exist "%INSTALLER_ZIP_BASE_DIR%" %PRINT_FAILURE_MESSAGE% "ERROR: Installation directory does not exist" && exit /b 1

set INSTALLER_ZIP_DIR=%INSTALLER_ZIP_BASE_DIR%\OneClipboardServer

rem :: skip if destination directory doesn't exist
if not exist "%INSTALLER_ZIP_DIR%" %PRINT_FAILURE_MESSAGE% "ERROR: Installation directory does not exist" && exit /b 1

rem :: -------------------------------------------
rem :: redirect "user.home" to a relative path
rem :: -------------------------------------------

set PORTABLE_DATA_DIR=%INSTALLER_ZIP_DIR%\.data

if not exist "%PORTABLE_DATA_DIR%" mkdir "%PORTABLE_DATA_DIR%"

set ONE_CLIPBOARD_SERVER_OPTS="-Duser.home=%PORTABLE_DATA_DIR%"

rem :: -------------------------------------------
rem :: run
rem :: -------------------------------------------

call "%INSTALLER_ZIP_DIR%\bin\OneClipboardServer.bat"

%PRINT_SUCCESS_MESSAGE% && exit /b 0
