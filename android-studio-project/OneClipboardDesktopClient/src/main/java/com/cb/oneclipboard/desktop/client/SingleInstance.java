package com.cb.oneclipboard.desktop.client;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;

public class SingleInstance {

  // https://stackoverflow.com/a/46668997
  public static void lock(String lockFileName) throws IOException {
    File lockFile = new File(lockFileName);

    FileChannel fc = FileChannel.open(
      lockFile.toPath(),
      StandardOpenOption.CREATE,
      StandardOpenOption.WRITE
    );

    FileLock lock = fc.tryLock();

    if (lock == null) {
      System.err.println("Application already running");
      System.exit(0);
    }
  }

}
