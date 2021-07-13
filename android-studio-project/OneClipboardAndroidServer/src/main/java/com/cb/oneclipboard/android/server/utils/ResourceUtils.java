package com.cb.oneclipboard.android.server.utils;

import android.content.Context;

public class ResourceUtils {

  public static int getInteger(Context context, int id) {
    return context.getResources().getInteger(id);
  }

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
