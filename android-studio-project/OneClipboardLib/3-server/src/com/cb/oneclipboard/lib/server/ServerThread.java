package com.cb.oneclipboard.lib.server;

import com.cb.oneclipboard.lib.common.Message;
import com.cb.oneclipboard.lib.common.MessageType;
import com.cb.oneclipboard.lib.common.User;
import com.cb.oneclipboard.lib.server.utils.StringUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        LOGGER.info(String.format(
          "Received '%s' from %s@%s",
          abbMessageText, message.getUser().getUserName(), getHostAddress()
        ));

        if (message.getMessageType() == MessageType.REGISTER) {
          user = message.getUser();

          LOGGER.info(String.format(
            "User SHA-256: %s",
            StringUtils.abbreviate(user.getSha256Hash(), 40)
          ));
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
          if (message.getText().equals("ping")) {
            try {
              ServerThread.this.pong();
            } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Error in sending message", e);
            }
          }
          else if (message.getText().equals("pong")) {
            // TODO: stop timeout timer
          }
        } else if (message.getMessageType() == MessageType.DISCONNECT) {
          close();
          break;
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

    // TODO: start timeout timer
    //       close() when timer reaches 0
  }

  public void pong() throws Exception {
    send(new Message("pong", MessageType.PING, user));
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
