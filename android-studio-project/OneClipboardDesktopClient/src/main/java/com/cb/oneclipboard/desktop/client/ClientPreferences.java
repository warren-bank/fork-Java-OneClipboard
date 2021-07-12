package com.cb.oneclipboard.desktop.client;

import com.cb.oneclipboard.lib.ApplicationProperties;

import java.util.prefs.Preferences;

/**
 * Created by krishnaraj on 16/12/15.
 */
public class ClientPreferences {
    private static final String HOST     = "HOST";
    private static final String PORT     = "PORT";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    Preferences preferences;

    public ClientPreferences() {
        preferences = Preferences.userRoot().node(this.getClass().getName());
    }

    public void setHost(String host) {
        preferences.put(HOST, host);
    }

    public String getHost() {
        return preferences.get(HOST, ApplicationProperties.getStringProperty("server"));
    }

    public void setPort(int port) {
        preferences.putInt(PORT, port);
    }

    public int getPort() {
        return preferences.getInt(PORT, ApplicationProperties.getIntProperty("server_port"));
    }

    public void setUsername(String username) {
        preferences.put(USERNAME, username);
    }

    public String getUsername() {
        return preferences.get(USERNAME, null);
    }

    public void setPassword(String password) {
        preferences.put(PASSWORD, password);
    }

    public String getPassword() {
        return preferences.get(PASSWORD, null);
    }

    public void clear() {
        preferences.remove(HOST);
        preferences.remove(PORT);
        preferences.remove(USERNAME);
        preferences.remove(PASSWORD);
    }
}
