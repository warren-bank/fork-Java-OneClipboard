### [OneClipboard](https://github.com/warren-bank/fork-Java-OneClipboard)

A collection of collaborative apps to automatically sync clipboards on remote devices.

* client / server architecture
  - all individual components are:
    * written entirely in Java
    * cross-platform
  - the server:
    * has no knowledge-of or access-to any clipboards
    * it simply relays text between connected clients
  - each client:
    * connects to a server
    * monitors the clipboard for changes
      - broadcasts new text to the server
    * listens for messages from the server
      - copies new text to the clipboard
* any number of clients can connect to a single server
* all clients that are connected to the same server, and identify with the same username/password combination:
  - will effectively share a single clipboard
    * an update to any one, will immediately update all
* all clipboard text communicated between devices is encrypted over the network

#### Usage

1. start an instance of either server (ie: desktop or Android)
2. start any combination of 2 or more clients
   - connect them all to the same server
   - enter the same username/password combination
3. copy some text in one client
4. paste the text in the other client(s)

- - - - -

#### Tasker Integration

Android client integrates with Tasker to function as an event plugin.

* configuration options:
  - clipboard changed:
    * remotely
      - plugin will only fire an event when the clipboard is changed<br>
        as the result of the client receiving data from the server
    * locally
      - plugin will only fire an event when the clipboard is changed<br>
        as the result of the local user performing a copy operation,<br>
        which has been sent to the server and dispersed to other clients
    * either
      - plugin will fire an event whenever the clipboard is changed,<br>
        either remotely or locally
* local variables:
  - `%clip`
    * value of the clipboard after an update occurs
* additional notes:
  - the Tasker event will only fire while the OneClipboard foreground service is running

#### Tasker Usage Example #1

* Tasker event:
  - Plugin &gt; OneClipboard
    * clipboard changed = remotely
* Tasker action:
  - Alert &gt; Popup
    * title = `OneClipboard:`
    * text  = `%clip`

#### Tasker Usage Example #2

* Tasker includes the action:
  - System &gt; Set Clipboard
* when this action executes,<br>OneClipboard will:
  - detect the update (if the OneClipboard foreground service is running)
  - forward the new value to the server (if connected)

- - - - -

#### Build Requirements

1. Java JDK 1.8+
2. Gradle 4.10.1
3. Android Build Tools 28.0.3

#### Build Instructions

```bash
# ================================================
# compile libraries shared by server and clients
# ================================================
./gradlew :OneClipboardLibCommon:compileJava
./gradlew :OneClipboardLibClient:compileJava
./gradlew :OneClipboardLibServer:compileJava
./gradlew :OneClipboardLibDesktop:compileJava

# ================================================
# generate:
#   OneClipboardDesktopServer/build/distributions/OneClipboardDesktopServer-3.01.01.zip
#   OneClipboardDesktopServer/build/distributions/OneClipboardDesktopServer-3.01.01-installer.jar
# ================================================
./gradlew :OneClipboardDesktopServer:compileJava
./gradlew :OneClipboardDesktopServer:processResources
./gradlew :OneClipboardDesktopServer:izPackCreateInstaller

# ================================================
# generate:
#   OneClipboardDesktopClient/build/distributions/OneClipboardDesktopClient-3.01.01.zip
#   OneClipboardDesktopClient/build/distributions/OneClipboardDesktopClient-3.01.01-installer.jar
# ================================================
./gradlew :OneClipboardDesktopClient:compileJava
./gradlew :OneClipboardDesktopClient:processResources
./gradlew :OneClipboardDesktopClient:izPackCreateInstaller

# ================================================
# generate:
#   OneClipboardAndroidServer/build/outputs/apk/release/OneClipboardAndroidServer-release-unsigned.apk
# ================================================
./gradlew :OneClipboardAndroidServer:compileReleaseJavaWithJavac
./gradlew :OneClipboardAndroidServer:assembleRelease

# ================================================
# generate:
#   OneClipboardAndroidServer/build/outputs/apk/release/OneClipboardAndroidServer-release.apk
# ================================================
source ~/load-android-keystore-credentials.sh

apk_dir='OneClipboardAndroidServer/build/outputs/apk/release'
apk_src="${apk_dir}/OneClipboardAndroidServer-release-unsigned.apk"
apk_dst="${apk_dir}/OneClipboardAndroidServer-release.apk"

apksigner sign                      \
  --v1-signing-enabled true         \
  --v2-signing-enabled true         \
  --v3-signing-enabled true         \
  --ks "$keystore_file"             \
  --ks-key-alias "$keystore_alias"  \
  --ks-pass "pass:${keystore_pass}" \
  --pass-encoding "utf-8"           \
  --key-pass "pass:${key_pass}"     \
  --out "$apk_dst"                  \
  "$apk_src"

# ================================================
# generate:
#   OneClipboardAndroidClient/build/outputs/apk/release/OneClipboardAndroidClient-release-unsigned.apk
# ================================================
./gradlew :OneClipboardAndroidClient:compileReleaseJavaWithJavac
./gradlew :OneClipboardAndroidClient:assembleRelease

# ================================================
# generate:
#   OneClipboardAndroidClient/build/outputs/apk/release/OneClipboardAndroidClient-release.apk
# ================================================
source ~/load-android-keystore-credentials.sh

apk_dir='OneClipboardAndroidClient/build/outputs/apk/release'
apk_src="${apk_dir}/OneClipboardAndroidClient-release-unsigned.apk"
apk_dst="${apk_dir}/OneClipboardAndroidClient-release.apk"

apksigner sign                      \
  --v1-signing-enabled true         \
  --v2-signing-enabled true         \
  --v3-signing-enabled true         \
  --ks "$keystore_file"             \
  --ks-key-alias "$keystore_alias"  \
  --ks-pass "pass:${keystore_pass}" \
  --pass-encoding "utf-8"           \
  --key-pass "pass:${key_pass}"     \
  --out "$apk_dst"                  \
  "$apk_src"
```

- - - - -

#### Installation Requirements

1. desktop server:
   * Java JRE 1.7+
2. desktop client:
   * Java JRE 1.7+
3. Android server:
   * Android 1.0 (API level 1)
4. Android client:
   * Android 4.0 (Ice Cream Sandwich, API level 14)

#### Installation Instructions

1. desktop server:
   * with the installer:
     ```bash
       java -jar /path/to/OneClipboardDesktopServer-3.01.01-installer.jar

       # (1) specify the directory where the .zip file should be uncompressed
       # (2) choose whether or not to create desktop shortcuts, and such
     ```
   * without the installer:
     ```bash
       # (1) manually uncompress the .zip file to a desired directory
       destination_dir=~/PortableApps/OneClipboardDesktopServer

       if [ ! -d  "$destination_dir" ];then
         mkdir -p "$destination_dir"
       fi

       unzip /path/to/OneClipboardDesktopServer-3.01.01.zip -d "$destination_dir"

       # (2) this configuration is optional;
       # setting the "user.home" property to a directory path that is relative to the program
       # prevents data files from being written to the HOME directory for the current user.
       portable_data_dir="${destination_dir}/.data"

       if [ ! -d  "$portable_data_dir" ];then
         mkdir -p "$portable_data_dir"
       fi

       export ONE_CLIPBOARD_DESKTOP_SERVER_OPTS="-Duser.home=${portable_data_dir}"

       # (3) run the desktop server using the /bin script provided for the shell (bash or Windows cmd)
       "${destination_dir}/bin/OneClipboardDesktopServer"
     ```
2. desktop client:
   * with the installer:
     ```bash
       java -jar /path/to/OneClipboardDesktopClient-3.01.01-installer.jar

       # (1) specify the directory where the .zip file should be uncompressed
       # (2) choose whether or not to create desktop shortcuts, and such
     ```
   * without the installer:
     ```bash
       # (1) manually uncompress the .zip file to a desired directory
       destination_dir=~/PortableApps/OneClipboardDesktopClient

       if [ ! -d  "$destination_dir" ];then
         mkdir -p "$destination_dir"
       fi

       unzip /path/to/OneClipboardDesktopClient-3.01.01.zip -d "$destination_dir"

       # (2) this configuration is optional;
       # setting the "user.home" property to a directory path that is relative to the program
       # prevents data files from being written to the HOME directory for the current user.
       portable_data_dir="${destination_dir}/.data"

       if [ ! -d  "$portable_data_dir" ];then
         mkdir -p "$portable_data_dir"
       fi

       export ONE_CLIPBOARD_DESKTOP_CLIENT_OPTS="-Duser.home=${portable_data_dir}"

       # (3) run the desktop client using the /bin script provided for the shell (bash or Windows cmd)
       "${destination_dir}/bin/OneClipboardDesktopClient"
     ```
3. Android server:
   * with adb
     ```bash
       adb install 'OneClipboardAndroidServer/build/apk/OneClipboardAndroidServer-release.apk'
     ```
   * with httpd or ftpd
     - download apk to Android device
     - [sideload](https://phandroid.com/2013/07/20/android-101-sideloading-apps/) apk
4. Android client:
   * with adb
     ```bash
       adb install 'OneClipboardAndroidClient/build/apk/OneClipboardAndroidClient-release.apk'
     ```
   * with httpd or ftpd
     - download apk to Android device
     - [sideload](https://phandroid.com/2013/07/20/android-101-sideloading-apps/) apk

- - - - -

#### License: [LGPL-3.0](./LICENSE.txt)
