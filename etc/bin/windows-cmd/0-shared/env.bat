@echo off

rem :: run once
if defined ANDROID_STUDIO_PROJECT exit /b 0

set ONECLIPBOARD_VERSION=1.03.01

set JDK_HOME=C:\Android\android-studio\jre
set JRE_HOME=%JDK_HOME%\jre
set CLASSPATH=%JRE_HOME%\lib;%JRE_HOME%\lib\rt.jar;%JDK_HOME%\lib;%JDK_HOME%\lib\tools.jar
set PATH=%JRE_HOME%\bin;%JDK_HOME%\bin;%PATH%

set ANDROID_BUILD_TOOLS_HOME=C:\Android\sdk\build-tools\30.0.3
set PATH=%ANDROID_BUILD_TOOLS_HOME%;%PATH%

set ANDROID_KEYSTORE_CREDENTIALS=C:\Android\load-android-keystore-credentials.bat

set ANDROID_STUDIO_PROJECT=%~dp0..\..\..\..\android-studio-project

set INSTALLER_ZIP_BASE_DIR=C:\PortableApps

rem :: ZIP installers require the "7z" 7-zip binary
set ZIP7_HOME=C:\PortableApps\7-Zip\16.02\App\7-Zip64
set PATH=%ZIP7_HOME%;%PATH%

rem :: gated utility methods
set SHOW_SUCCESS_MESSAGE=1
set SHOW_FAILURE_MESSAGE=1
set PRINT_SUCCESS_MESSAGE=if defined SHOW_SUCCESS_MESSAGE call "%~dp0.\show-message.bat"
set PRINT_FAILURE_MESSAGE=if defined SHOW_FAILURE_MESSAGE call "%~dp0.\show-message.bat"
