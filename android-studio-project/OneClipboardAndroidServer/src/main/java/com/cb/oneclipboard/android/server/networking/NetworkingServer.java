package com.cb.oneclipboard.android.server.networking;

import com.cb.oneclipboard.android.server.networking.logging.AndroidLoggingHandler;

import com.cb.oneclipboard.lib.server.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkingServer extends Server {

    public static void init(int serverPort, Callback callback) {
        // defer Java logging to Android logging
        AndroidLoggingHandler.reset(new AndroidLoggingHandler());
        Logger.getLogger("").setLevel(Level.FINEST);

        Server.init(serverPort, callback);
    }

}
