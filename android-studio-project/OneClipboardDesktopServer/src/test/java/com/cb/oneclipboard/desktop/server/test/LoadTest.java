package com.cb.oneclipboard.desktop.server.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import com.cb.oneclipboard.lib.common.Message;
import com.cb.oneclipboard.lib.client.socket.ClipboardConnector;
import com.cb.oneclipboard.lib.client.socket.SocketListener;

public class LoadTest {
  private static final int NO_OF_CLIENTS = 100;
  private CountDownLatch latch = null;
  private String serverAddress = null;
  private int serverPort;

  @Before
  public void setUp() throws Exception {
    serverAddress = "localhost";
    serverPort = 4545;
  }

  @Test
  public void test() throws InterruptedException {
    latch = new CountDownLatch(NO_OF_CLIENTS);
    
    for (int i = 0; i < NO_OF_CLIENTS; i++) {
      addClient();
    }

    latch.await();

    Thread.sleep(1000 * 60);

    assertTrue(true);
  }

  private void addClient() {
    ClipboardConnector clipboardConnector = new ClipboardConnector();
    clipboardConnector
      .server(serverAddress)
      .port(serverPort)
      .socketListener(new SocketListener() {
        @Override
        public void onMessageReceived(Message message) {
          // TODO Auto-generated method stub
        }

        @Override
        public void onDisconnect() {
          // TODO Auto-generated method stub
        }

        @Override
        public void onConnect() {
          latch.countDown();
        }
      })
      .connect()
    ;
  }

}
