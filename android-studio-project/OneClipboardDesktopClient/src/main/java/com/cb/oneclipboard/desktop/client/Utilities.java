package com.cb.oneclipboard.desktop.client;

import com.cb.oneclipboard.lib.desktop.ApplicationProperties;

public class Utilities {

  public static String getFullApplicationName() {
    return ApplicationProperties.getStringProperty("app_name") + " " + ApplicationProperties.getStringProperty("version");
  }
}