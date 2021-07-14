package com.cb.oneclipboard.lib.desktop;

import java.util.Properties;

public class ApplicationProperties {

  static Properties properties = new Properties();
  
  public static void loadProperties(String[] fileList, PropertyLoader loader) {
    for (int i = fileList.length - 1; i >= 0; i--) {
      loader.load(properties, fileList[i]);
    }
  }
  
  public static String getStringProperty(String key) {
    return properties.getProperty(key);
  }
  
  public static Integer getIntProperty(String key) {
    return Integer.parseInt(getStringProperty(key));
  }
  

}
