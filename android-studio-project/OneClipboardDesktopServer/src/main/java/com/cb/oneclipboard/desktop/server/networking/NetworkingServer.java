package com.cb.oneclipboard.desktop.server.networking;

import com.cb.oneclipboard.desktop.server.networking.logging.LevelBasedFileHandler;

import com.cb.oneclipboard.lib.server.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkingServer extends Server {

    public static void init(int serverPort, Callback callback) {
        try {
            // initialize Logger
            Logger.getLogger("").addHandler(new LevelBasedFileHandler("%h/oneclipboardserver.log", Arrays.asList(Level.INFO, Level.WARNING)));
            Logger.getLogger("").addHandler(new LevelBasedFileHandler("%h/oneclipboardserver.err", Arrays.asList(Level.SEVERE)));
        }
        catch(IOException e) {
            LOGGER.log(Level.SEVERE, "Error initializing Logger", e);
        }

        Server.init(serverPort, callback);
    }

}
