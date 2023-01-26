@echo off

call "%~dp0..\0-shared\env.bat"

cd /D "%ANDROID_STUDIO_PROJECT%"

call gradlew.bat :OneClipboardAndroidClient:compileReleaseJavaWithJavac
call gradlew.bat :OneClipboardAndroidClient:assembleRelease

rem :: ---------------------------------
rem :: sign
rem :: ---------------------------------

rem :: skip if credentials are not available
if not defined ANDROID_KEYSTORE_CREDENTIALS   %PRINT_SUCCESS_MESSAGE% "WARNING: No keystore to sign the apk" && exit /b 0
if not exist "%ANDROID_KEYSTORE_CREDENTIALS%" %PRINT_SUCCESS_MESSAGE% "WARNING: No keystore to sign the apk" && exit /b 0

set APK_DIR=OneClipboardAndroidClient\build\outputs\apk\release
set APK_SRC=%APK_DIR%\OneClipboardAndroidClient-release-unsigned.apk
set APK_DST=%APK_DIR%\OneClipboardAndroidClient-release.apk

rem :: skip if unsigned apk was not generated by previous step
if not exist "%APK_SRC%" %PRINT_FAILURE_MESSAGE% "ERROR: No unsigned apk generated by build" && exit /b 1

call "%ANDROID_KEYSTORE_CREDENTIALS%"

call apksigner.bat sign --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true --ks "%keystore_file%" --ks-key-alias "%keystore_alias%" --ks-pass "pass:%keystore_pass%" --pass-encoding "utf-8" --key-pass "pass:%key_pass%" --out "%APK_DST%" "%APK_SRC%"

%PRINT_SUCCESS_MESSAGE% && exit /b 0