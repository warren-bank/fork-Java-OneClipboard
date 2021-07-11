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

#### Build Requirements

1. Java 1.7+
2. Gradle 4.10.1
3. Android Build Tools 28.0.3

#### Build Instructions

```bash
# ================================================
# compile library shared by server and clients
# ================================================
./gradlew :oneclipboardlib:compileJava

# ================================================
# generate:
#   oneclipboardserver/build/distributions/OneClipboardServer-1.2.0.zip
#   oneclipboardserver/build/distributions/oneclipboardserver-1.2.0-installer.jar
# ================================================
./gradlew :oneclipboardserver:compileJava
./gradlew :oneclipboardserver:processResources
./gradlew :oneclipboardserver:izPackCreateInstaller

# ================================================
# generate:
#   oneclipboarddesktop/build/distributions/OneClipboardDesktop-1.2.0.zip
#   oneclipboarddesktop/build/distributions/oneclipboarddesktop-1.2.0-installer.jar
# ================================================
./gradlew :oneclipboarddesktop:compileJava
./gradlew :oneclipboarddesktop:processResources
./gradlew :oneclipboarddesktop:izPackCreateInstaller

# ================================================
# generate:
#   oneclipboardandroid/build/outputs/apk/release/oneclipboardandroid-release-unsigned.apk
# ================================================
./gradlew :oneclipboardandroid:compileReleaseJavaWithJavac

# ================================================
# generate:
#   oneclipboardandroid/build/outputs/apk/release/oneclipboardandroid-release.apk
# ================================================
source ~/load-android-keystore-credentials.sh

apk_dir='oneclipboardandroid/build/outputs/apk/release'
apk_src="${apk_dir}/oneclipboardandroid-release-unsigned.apk"
apk_dst="${apk_dir}/oneclipboardandroid-release.apk"

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

#### Installation Requirements

1. Android 4.1 (Jelly Bean, API level 16)

#### Installation Instructions

1. desktop server:
   * with the installer:
     ```bash
       java -jar /path/to/oneclipboardserver-1.2.0-installer.jar

       # (1) specify the directory where the .zip file should be uncompressed
       # (2) choose whether or not to create desktop shortcuts, and such
     ```
   * without the installer:
     ```bash
       # (1) manually uncompress the .zip file to a desired directory
       destination_dir=~/PortableApps/OneClipboardServer

       if [ ! -d  "$destination_dir" ];then
         mkdir -p "$destination_dir"
       fi

       unzip /path/to/OneClipboardServer-1.2.0.zip -d "$destination_dir"

       # (2) this configuration is optional;
       # setting the "user.home" property to a directory path that is relative to the program
       # prevents data files from being written to the HOME directory for the current user.
       portable_data_dir="${destination_dir}/.data"

       if [ ! -d  "$portable_data_dir" ];then
         mkdir -p "$portable_data_dir"
       fi

       export ONE_CLIPBOARD_SERVER_OPTS="-Duser.home=${portable_data_dir}"

       # (3) run the desktop server using the /bin script provided for the shell (bash or Windows cmd)
       "${destination_dir}/bin/OneClipboardServer"
     ```
2. desktop client:
   * with the installer:
     ```bash
       java -jar /path/to/oneclipboarddesktop-1.2.0-installer.jar

       # (1) specify the directory where the .zip file should be uncompressed
       # (2) choose whether or not to create desktop shortcuts, and such
     ```
   * without the installer:
     ```bash
       # (1) manually uncompress the .zip file to a desired directory
       destination_dir=~/PortableApps/OneClipboardDesktop

       if [ ! -d  "$destination_dir" ];then
         mkdir -p "$destination_dir"
       fi

       unzip /path/to/OneClipboardDesktop-1.2.0.zip -d "$destination_dir"

       # (2) this configuration is optional;
       # setting the "user.home" property to a directory path that is relative to the program
       # prevents data files from being written to the HOME directory for the current user.
       portable_data_dir="${destination_dir}/.data"

       if [ ! -d  "$portable_data_dir" ];then
         mkdir -p "$portable_data_dir"
       fi

       export ONE_CLIPBOARD_DESKTOP_OPTS="-Duser.home=${portable_data_dir}"

       # (3) run the desktop client using the /bin script provided for the shell (bash or Windows cmd)
       "${destination_dir}/bin/OneClipboardDesktop"
     ```
3. Android client:
   * with adb
     ```bash
       adb install 'oneclipboardandroid/build/apk/oneclipboardandroid-release.apk'
     ```
   * with httpd or ftpd
     - download apk to Android device
     - [sideload](https://phandroid.com/2013/07/20/android-101-sideloading-apps/) apk

#### Usage

1. start the desktop server
2. start any combination of 2+ clients
   - connect them all to the same server
   - enter the same username/password combination
3. copy some text in one client
4. paste the text in the other client(s)
