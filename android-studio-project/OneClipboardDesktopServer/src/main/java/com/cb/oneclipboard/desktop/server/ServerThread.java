package com.cb.oneclipboard.desktop.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cb.oneclipboard.lib.Message;
import com.cb.oneclipboard.lib.MessageType;
import com.cb.oneclipboard.lib.User;
import org.apache.commons.lang3.StringUtils;

public class ServerThread extends Thread {
  private final static Logger LOGGER = Logger.getLogger(ServerThread.class.getName());
  
  Socket socket = null;
  ObjectInputStream objInputStream = null;
  ObjectOutputStream objOutputStream = null;
  User user = null;

  public ServerThread(Socket socket) throws Exception {
    super("ServerThread");
    this.socket = socket;
    LOGGER.info(String.format(
      "Accepting connection from %s",
      getHostAddress()
    ));
    start();
  }

  @Override
  public void run() {
    try {
      objInputStream = new ObjectInputStream(socket.getInputStream());
      objOutputStream = new ObjectOutputStream(socket.getOutputStream());
      objOutputStream.flush();
      
      Registery.register(this);
      
      Message message;
      while ((message = (Message) objInputStream.readObject()) != null) {
        String abbMessageText = StringUtils.abbreviate(message.getText(), 10);
        String abbSha256Hash  = StringUtils.abbreviate(message.getUser().getSha256Hash(), 10);
        LOGGER.info(String.format(
          "Received '%s' from %s:%s@%s",
          abbMessageText, message.getUser().getUserName(), abbSha256Hash, getHostAddress()
        ));

        if (message.getMessageType() == MessageType.REGISTER) {
          user = message.getUser();
        } else if (message.getMessageType() == MessageType.CLIPBOARD_TEXT && user != null) { // if we haven't received the register message than ignore the text messages
          for (ServerThread serverThread : Registery.getClientSockets()) {
            if (!this.socket.equals(serverThread.socket) && this.user.equals(serverThread.user)) {
              LOGGER.info(String.format(
                "Sending '%s' to %s@%s",
                abbMessageText, serverThread.user.getUserName(), serverThread.getHostAddress()
              ));
              try {
                serverThread.send(message);
              } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in sending message", e);
              }
            }
          }
        } else if (message.getMessageType() == MessageType.PING) {
          // Ping received from client
        }
      }

    } catch (Exception e) {
      if (e.getMessage() != null && e.getMessage().equals("Connection reset")) {
        LOGGER.info(String.format(
          "Lost connection to client: %s@%s",
          user.getUserName(), getHostAddress()
        ));
      }
      else {
        LOGGER.log(Level.SEVERE, "Error reading stream", e);
      }
      close();
    }
  }

  public void ping() throws Exception {
    send(new Message("ping", MessageType.PING, user));
  }

  protected synchronized void send(Message message) throws Exception {
    objOutputStream.writeObject(message);
    objOutputStream.flush();
  }

  public String getHostAddress() {
    return socket.getInetAddress().getHostAddress();
  }

  public void close() {
    try {
      Registery.getClientSockets().remove(this);
      objInputStream.close();
      objOutputStream.close();
      socket.close();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, String.format(
        "Connection is already closed to client: %s@%s",
        user.getUserName(), getHostAddress()
      ));
    }
  }
}
