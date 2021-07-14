package com.cb.oneclipboard.android.client.util;

import com.cb.oneclipboard.lib.client.socket.ClipboardConnector;

public class Utility {
  
  public static String getConnectionStatus(ClipboardConnector clipboardConnector) {
    if( clipboardConnector != null && clipboardConnector.isConnected() ) {
      return "Connected to " + clipboardConnector.getServerName();
    } else {
      return "Not connected.";
    }
  }
}
