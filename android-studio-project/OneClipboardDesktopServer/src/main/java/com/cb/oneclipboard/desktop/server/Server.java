package com.cb.oneclipboard.desktop.server;

import com.cb.oneclipboard.desktop.server.networking.NetworkingServer;

import com.cb.oneclipboard.lib.desktop.ApplicationProperties;
import com.cb.oneclipboard.lib.desktop.DefaultPropertyLoader;

public class Server {
    private static final String[] PROP_LIST = {"config.properties"};
    private static boolean isShuttingDown = false;

    public static void main(String[] args) {
        // Load properties
        ApplicationProperties.loadProperties(PROP_LIST, new DefaultPropertyLoader());

        int serverPort = ApplicationProperties.getIntProperty("server_port");

        NetworkingServer.init(serverPort, new NetworkingServer.Callback(){
            @Override
            public void stopped() {
                if (!isShuttingDown) {
                    System.exit(0);
                }
            }
        });

        NetworkingServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                isShuttingDown = true;
                NetworkingServer.stop();
            }
        });
    }

}
