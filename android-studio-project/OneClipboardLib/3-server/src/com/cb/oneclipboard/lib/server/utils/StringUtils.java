package com.cb.oneclipboard.lib.server.utils;

public class StringUtils {

  public static String abbreviate(String text, int maxLength) {
    if (text == null)
      return "";

    if (text.length() <= maxLength)
      return text;

    if (text.length() <= 8)
      return text.substring(0, maxLength);

    return text.substring(0, maxLength - 3) + "...";
  }

}
