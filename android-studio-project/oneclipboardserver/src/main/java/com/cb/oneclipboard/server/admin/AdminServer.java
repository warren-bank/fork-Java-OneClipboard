package com.cb.oneclipboard.server.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminServer {
  private final static Logger LOGGER = Logger.getLogger(AdminServer.class.getName());
  
  private static final int serverPort = 4040;

  public static void start(String args[]) throws Exception {
    Thread adminThread = new Thread(new Runnable() {
      
      @Override
      public void run() {
        ServerSocket serverSocket = null;
        try {
          serverSocket = new ServerSocket(serverPort);
          LOGGER.info("Admin Server started on port: " + serverPort);
        } catch (IOException e) {
          // Port is in use.
          // Probably indicates that an instance of this program is already running in a different process.
          // Since the admin server isn't vital, don't kill this process yet.
          // If the port used for client connections (ex: 4545) is also in use, then this process will be killed.

          LOGGER.severe("Error starting admin server. Could not listen on port: " + serverPort);
          return;
        }

        while (true) {
          Socket clientSocket = null;
          try {
            clientSocket = serverSocket.accept();
          } catch (SocketException e) {
              break; // exit loop
          } catch (Exception e) {
            LOGGER.severe("Admin Server accept failed");
            continue;
          }
          try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            AdminCommandProcessor commandProcessor = new AdminCommandProcessor();

            while ((inputLine = in.readLine()) != null) {
              outputLine = commandProcessor.processCommand(inputLine);
              out.println(outputLine);
              if (outputLine.equals("exit"))
                break;
            }

            out.close();
            in.close();
            clientSocket.close();
          } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception in AdminServer Thread", e);
          }
        }
      }
    }, "Admin Server Thread");
    
    adminThread.start();
  }
}
