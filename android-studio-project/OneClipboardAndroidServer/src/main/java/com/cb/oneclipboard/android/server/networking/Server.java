package com.cb.oneclipboard.android.server.networking;

import com.cb.oneclipboard.android.server.networking.admin.AdminServer;
import com.cb.oneclipboard.android.server.networking.logging.AndroidLoggingHandler;

import java.net.ServerSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    public interface Callback {
        public void stopped();
    }

    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static int serverPort;
    private static ServerSocket serverSocket = null;
    private static Callback callback = null;

    public static void init(int serverPort, Callback callback) {
        Server.serverPort = serverPort;
        Server.callback   = callback;

        // set up loggers
        AndroidLoggingHandler.reset(new AndroidLoggingHandler());
        LOGGER.setLevel(Level.FINEST);

        try {
            // Start admin server
            AdminServer.start();
            ServerThreadCleaner.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting admin server", e);
        } finally {
        }
    }

    public static void start() {
        Thread startupThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(serverPort);
                    LOGGER.info("Server started on port: " + serverPort);
                } catch (Exception e) {
                    // Port is in use.
                    // Probably indicates that an instance of this program is already running in a different process.

                    LOGGER.severe("Error starting server. Could not listen on port: " + serverPort);
                    callback.stopped();
                    return;
                }

                while (!serverSocket.isClosed()) {
                    ServerThread serverThread = null;
                    try {
                        serverThread = new ServerThread(serverSocket.accept());
                    } catch (SocketException e) {
                        break; // exit loop
                    } catch (Exception e) {
                        try {
                            serverThread.close();
                        } catch (Exception ex) {
                            LOGGER.log(Level.WARNING, "Unable to close server thread properly:\n" + ex.getMessage());
                        }
                        LOGGER.log(Level.WARNING, "Lost connection to client:\n" + e.getMessage());
                    }
                }
            }
        }, "Startup Thread");
        startupThread.start();
    }

    public static boolean stop() {
        boolean OK = true;

        if (serverSocket.isClosed())
            return OK;

        try {
            LOGGER.info("Stopping server...");
            serverSocket.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Stop failed!", e);
            OK = false;
        }
        callback.stopped();
        return OK;
    }

    public static boolean restart() {
        try {
            LOGGER.info("Restarting server...");
            if (!serverSocket.isClosed())
                serverSocket.close();
            start();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Restart failed!", e);
            return false;
        }
    }

}
