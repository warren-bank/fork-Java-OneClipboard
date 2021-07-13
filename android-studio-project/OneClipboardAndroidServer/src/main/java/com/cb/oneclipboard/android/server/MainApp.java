package com.cb.oneclipboard.android.server;

import android.app.Application;

public class MainApp extends Application {
  private static MainApp instance;

  public static MainApp getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
  }
}
