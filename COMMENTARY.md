### Personal Commentary

I recently went on a bit of a quest.
I was looking for software to easily synchronize clipboard text between desktop/laptop computers and Android devices.

#### My criteria included:

* 100% open-source
  - so I could verify that it's safe to use
* written in Java
  - so it's cross-platform
  - easy to compile
* executables should be small and lightweight to run
* no cloud services
  - only want it to work within a LAN
  - clients should be able to detect servers,
    and present a list of available options
* bi-directional sync
* data encryption

#### Top contenders:

* ClipSync
  - overview:
    * by: [Jules Colle](https://github.com/pwkip)
    * [Android client](https://play.google.com/store/apps/details?id=be.bdwm.clipsync)
    * [Windows .NET server](https://clipsync.bdwm.be/ClipSyncServer3.msi)
  - pros:
    * APK is incredibly tiny
      - 147 KB
    * APK has a very low minSdkVersion
      - Android 2.1 (Eclair MR1, API 7)
    * APK scans for servers on LAN,
      and allows the user to select one from a list
    * doesn't use any cloud services
    * bi-directional sync
  - cons:
    * closed-source
    * server depends on the .NET Framework runtime
    * no data encryption
  - related:
    * there is an implementation of the server that was [backwards-engineered and rewritten in Python](http://fluffymcdeath.com/clipsync_on_linux.html)
* ClipSync
  - overview:
    * by: [Pishang Ujeniya](https://github.com/pishangujeniya)
    * [Android client](https://github.com/pishangujeniya/clipsync-android/releases)
    * [Windows .NET server](https://github.com/pishangujeniya/clipsync-windows/releases)
  - pros:
    * open-source
    * APK scans for servers on LAN,
      and allows the user to select one from a list
    * doesn't use any cloud services
    * bi-directional sync
  - cons:
    * APK is significantly larger than it should be
      - 3 MB
    * APK has a significantly higher minSdkVersion than it should be
      - Android 6.0 (Marshmallow, API 23)
    * server depends on the .NET Framework runtime
    * no data encryption
* Join
  - overview:
    * by: [João Dias](https://github.com/joaomgcd)
    * [Android client](https://play.google.com/store/apps/details?id=com.joaomgcd.join)
    * [Electron desktop client](https://github.com/joaomgcd/JoinDesktop)
    * _Google Cloud Platform_ relays messages between clients
  - pros:
    * bi-directional sync
    * data encryption
    * Tasker integration
  - cons:
    * closed-source
      - not free
    * requires WAN cloud services
      - account login is required
* __OneClipboard__
  - overview:
    * by: [Krishnaraj Jayachandran](https://github.com/krishnaraj)
      - blog post: [announcement](https://web.archive.org/web/20141117162826/http://blog.crushingboredom.com/oneclipboard/)
    * [Android client](https://play.google.com/store/apps/details?id=com.cb.oneclipboard)
    * [Java desktop client](https://web.archive.org/web/20160713011445/http://downloads.crushingboredom.com/oneclipboarddesktop-1.2.0-installer.jar)
    * Java server
      - source code is available
      - no binary was ever published
      - binaries pubished for both the Android and desktop clients hard-code the server's location:<br>`crushingboredom.com:4545`
  - pros:
    * open-source
      - quality of coding is excellent
    * written in Java
    * APK is acceptably small
      - 1.2 MB
    * APK has an acceptably low minSdkVersion
      - Android 4.1 (Jelly Bean, API 16)
    * doesn't use any cloud services
    * bi-directional sync
    * data encryption

#### The Winner

* __OneClipboard__
  - verdict:
    * very solid starting point
  - room for improvement:
    * the location of the server...
      - is currently (v1.2.0) hard-coded into all of the clients
        * in the [source code public repo](https://github.com/krishnaraj/oneclipboard/blob/cc8f4fe73b48708b4bc253058c03b0ef506ef164/gradle.properties#L7), this value is: `localhost:4545`
        * this was intended, because the author's goal was to host the server on a WAN
          - side note: his public server is currently unresponsive
      - the inclusion of Multicast DNS (mDNS) would be a big improvement
        * the server can register a Bonjour service on the LAN
        * every client (both Android and desktop) can discover all of the registered servers,
          and present a list of available options
          - once a server is chosen by the user, everything else would continue as it does now...
      - to make the clients as flexible as possible
        * in addition to servers dynamically discovered on the LAN,
          users could also be allowed to manually add static server locations, which could:
          - be persistent
          - be edited or removed by the user
          - be given a descriptive name to appear in the list
          - refer to an IP outside of the LAN
            * ex: `crushingboredom.com:4545`
    * the username/password combination...
      - is currently (v1.2.0) stored by clients (both [Android](https://github.com/krishnaraj/oneclipboard/blob/cc8f4fe73b48708b4bc253058c03b0ef506ef164/oneclipboardandroid/src/main/java/com/cb/oneclipboard/MainActivity.java#L26) and [desktop](https://github.com/krishnaraj/oneclipboard/blob/cc8f4fe73b48708b4bc253058c03b0ef506ef164/oneclipboarddesktop/src/main/java/com/cb/oneclipboard/desktop/Client.java#L75)) in persistent app preferences
        * after the user has entered values once, there is no way through the client UI to remove or change them
        * these values are automatically reused every time the client is restarted
      - the login form should always be presented to the user before connecting to a server
        * form fields can be prepopulated from persistent app preferences
      - side note: on Windows, the desktop client stores its preferences in the Windows Registry
        * key: `HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com.cb.oneclipboard.desktop./Client/Preferences`
    * the Android app needs some updates...
      - crashes on Android 8.0 (Oreo, API 26) and newer
        * missing a NotificationChannel
