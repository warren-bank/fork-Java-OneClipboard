package com.cb.oneclipboard.lib.client.socket;

import com.cb.oneclipboard.lib.common.Message;

public interface SocketListener {
  public void onMessageReceived(Message message);
  public void onConnect();
  public void onDisconnect();
}
