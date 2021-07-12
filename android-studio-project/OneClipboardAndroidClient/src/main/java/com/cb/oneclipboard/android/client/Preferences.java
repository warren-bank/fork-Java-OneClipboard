package com.cb.oneclipboard.android.client;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by krishnaraj on 16/12/15.
 */
public class Preferences {
    private static final String PREFERENCE_NAME = "OneClipboard";
    private static final String HOST     = "HOST";
    private static final String PORT     = "PORT";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    private SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setHost(String host) {
        write(HOST, host);
    }

    public String getHost() {
        return sharedPreferences.getString(HOST, null);
    }

    public void setPort(String port) {
        write(PORT, port);
    }

    public void setPortNumber(int port) {
        write(PORT, Integer.toString(port, 10));
    }

    public String getPort() {
        return sharedPreferences.getString(PORT, null);
    }

    public int getPortNumber() {
        String port = getPort();
        return (port == null) ? -1 : Integer.parseInt(port, 10);
    }

    public void setUsername(String username) {
        write(USERNAME, username);
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME, null);
    }

    public void setPassword(String password) {
        write(PASSWORD, password);
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, null);
    }

    private void write(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
