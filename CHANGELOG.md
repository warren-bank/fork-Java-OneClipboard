1.2.0
=====

* forked from:
  - repo: [oneclipboard](https://github.com/krishnaraj/oneclipboard)
  - commit: [cc8f4fe](https://github.com/krishnaraj/oneclipboard/commit/cc8f4fe73b48708b4bc253058c03b0ef506ef164)
    * date: 2017-04-09
* reorganized project directory
* updated verions of build tools (Gradle, Android, etc)
* made minor changes:
  - to Gradle build scripts
    * the server is now packaged in the same way as the desktop client
  - changed the directory for server logs from CWD to "user.home"
  - removed unused dependencies
    * related to SSL, which was [previously removed](https://github.com/krishnaraj/oneclipboard/commit/3c13c3e7f1f1432ca539cd88870b3dec76b1be07)
  - removed unused classes
    * related to SSL, which was [previously removed](https://github.com/krishnaraj/oneclipboard/commit/3c13c3e7f1f1432ca539cd88870b3dec76b1be07)
* issues:
  - server location is hard-coded into all clients: `localhost:4545`
    * users can test the desktop server with the desktop client on a single machine
    * users can test clients on other devices by editing their `hosts` file
      - to resolve `localhost` to a chosen IP address
      - in general, however, these clients aren't very useful
