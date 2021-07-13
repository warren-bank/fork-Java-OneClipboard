package com.cb.oneclipboard.android.server.networking.admin;

import com.cb.oneclipboard.android.server.networking.Registery;
import com.cb.oneclipboard.android.server.networking.Server;
import com.cb.oneclipboard.android.server.networking.ServerThread;

public class AdminCommandProcessor {

  public String processCommand(String command) {
    String output = "";
    if (command == null) {
      command = "";
    }

    switch (command) {
    case "client list":
      output = getFormattedClientList();
      break;
    case "client count":
      output = String.valueOf(getClientCount());
      break;
    case "restart":
      boolean success = Server.restart();
      output = success ? "Restart completed successfully" : "Restart failed";
      break;
    case "exit":
      output = "exit";
      break;
    default:
      output = "Unknown command.";
    }

    return output;
  }

  private String getFormattedClientList() {
    String result = "Connected clients:";

    if (Registery.getClientSockets().isEmpty()) {
      result = "\nNone.";
    } else {
      for (ServerThread serverThread : Registery.getClientSockets()) {
        result = result + "\n" + serverThread.getHostAddress();
      }
    }

    return result;
  }

  private int getClientCount() {
    return Registery.getClientSockets().size();
  }
}
