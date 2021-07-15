package com.cb.oneclipboard.android.client;

import com.cb.oneclipboard.android.client.tasker.TaskerIpc;
import com.cb.oneclipboard.android.client.util.IntentUtil;
import com.cb.oneclipboard.android.client.util.Utility;

import com.cb.oneclipboard.lib.common.*;
import com.cb.oneclipboard.lib.client.*;
import com.cb.oneclipboard.lib.client.socket.*;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ClipboardApplication extends Application {
    public static final int NOTIFICATION_ID = 1;
    public static final String CLIPBOARD_UPDATED = "clipboard_updated";
    private static final String TAG = ClipboardApplication.class.getName();
    public Preferences pref = null;
    private ClipboardConnector clipboardConnector = null;
    private ClipboardListener clipboardListener = null;
    private ClipboardManager clipBoard = null;
    private CipherManager cipherManager = null;
    private User user = null;
    private NotificationCompat.Builder notificationBuilder = null;
    private LocalBroadcastManager broadcaster = null;

    @Override
    public void onCreate() {
        super.onCreate();

        pref        = new Preferences(this);
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CipherManager getCipherManager() {
        return cipherManager;
    }

    public void setCipherManager(CipherManager cipherManager) {
        this.cipherManager = cipherManager;
    }

    public void initializeClipboardListener() {
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardListener = new ClipboardListener(clipBoard, new Callback() {

            @Override
            public void execute(Object object) {
                final String clipboardText = (String) object;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(new Message(cipherManager.encrypt(clipboardText), user));

                        // trigger Tasker event
                        sendBroadcast(
                          TaskerIpc.getRequestQueryIntent(clipboardText, /* updatedRemotelyByServer */ false)
                        );
                    }
                }).start();
            }
        });
        clipBoard.addPrimaryClipChangedListener(clipboardListener);
    }

    public void removeClipboardListener() {
        if (clipBoard != null && clipboardListener != null) {
            clipBoard.removePrimaryClipChangedListener(clipboardListener);
        }
        clipBoard         = null;
        clipboardListener = null;
    }

    public boolean isConnected() {
        return clipboardConnector != null && clipboardConnector.isConnected();
    }

    public void establishConnection() {
        Log.d(TAG, "Establishing connection to server...");

        try {
            // Listen for clipboard content from other clients
            clipboardConnector = new ClipboardConnector()
                    .server(pref.getHost())
                    .port(pref.getPortNumber())
                    .socketListener(new SocketListener() {

                        @Override
                        public void onMessageReceived(Message message) {
                            String clipboardText = cipherManager.decrypt(message.getText());
                            clipboardListener.updateClipboardContent(clipboardText);
                            clipBoard.setText(clipboardText);

                            // broadcast the message so that activities can update
                            Intent intent = new Intent(ClipboardApplication.CLIPBOARD_UPDATED);
                            intent.putExtra("message", clipboardText);
                            broadcaster.sendBroadcast(intent);

                            // trigger Tasker event
                            sendBroadcast(
                              TaskerIpc.getRequestQueryIntent(clipboardText, /* updatedRemotelyByServer */ true)
                            );
                        }

                        @Override
                        public void onConnect() {
                            send(new Message("register", MessageType.REGISTER, user));
                            updateNotification();
                        }

                        @Override
                        public void onDisconnect() {
                            Log.d(TAG, "disconnected!");
                            updateNotification();
                        }

                    })
                    .connect();

        } catch (Exception e) {
            Log.e(TAG, "Unable to establish connection", e);
        } finally {
        }
    }

    public void closeConnection() {
        if (isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    notificationBuilder = null;
                    send(new Message("disconnect", MessageType.DISCONNECT, user));
                    clipboardConnector.close();
                    clipboardConnector = null;
                }
            }).start();
        }
    }

    public void send(Message message) {
        if (isConnected()) {
            clipboardConnector.send(message);
        }
    }

    public NotificationCompat.Builder getNotificationBuilder(Context context) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, IntentUtil.getHomePageIntent(context), PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(context, getPackageName())
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return notificationBuilder;
    }

    public void updateNotification() {
        Log.d(TAG, "notificationBuilder: " + notificationBuilder);
        if (notificationBuilder != null) {
            notificationBuilder.setContentText(Utility.getConnectionStatus(clipboardConnector));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(ClipboardApplication.NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
