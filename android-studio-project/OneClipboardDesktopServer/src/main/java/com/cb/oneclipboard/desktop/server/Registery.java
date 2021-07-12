package com.cb.oneclipboard.desktop.server;

import java.util.Vector;

public class Registery {
  private static Vector<ServerThread> clientSockets = new Vector<>();
  
  public static void register(ServerThread serverThread){
    clientSockets.add(serverThread);
  }
  
  public static Vector<ServerThread> getClientSockets(){
    return clientSockets;
  }
  
}
