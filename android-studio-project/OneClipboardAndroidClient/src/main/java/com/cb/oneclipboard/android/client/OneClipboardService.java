package com.cb.oneclipboard.android.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class OneClipboardService extends Service {
  private static final String TAG = "OneClipboardService";

  public static volatile boolean isRunning = false;
  
  @Override
  public int onStartCommand( Intent intent, int flags, int startId ) {
    Log.d( TAG, "onStartCommand called" );
    
    showNotification();
    isRunning = true;
    start();

    return Service.START_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }
  
  @Override
  public void onDestroy() {
    hideNotification();
    isRunning = false;
    super.onDestroy();
  }

  @Override
  public IBinder onBind( Intent arg0 ) {
    return null;
  }

  public void start() {
    ( (ClipboardApplication) getApplicationContext() ).initializeClipboardListener();
    ( (ClipboardApplication) getApplicationContext() ).establishConnection();
  }

    // -------------------------------------------------------------------------
    // foregrounding..

    private Notification getNotification() {
        NotificationCompat.Builder notificationBuilder = ( (ClipboardApplication) getApplicationContext() ).getNotificationBuilder(this);
        return notificationBuilder.build();
    }

    private String getNotificationChannelId() {
        return getPackageName();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            String channelId       = getNotificationChannelId();
            NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel NC = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);

            NC.setDescription(channelId);
            NC.setSound(null, null);
            NM.createNotificationChannel(NC);
        }
    }

    private void showNotification() {
        Notification notification = getNotification();

        if (Build.VERSION.SDK_INT >= 5) {
            createNotificationChannel();
            startForeground(ClipboardApplication.NOTIFICATION_ID, notification);
        }
        else {
            NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NM.notify(ClipboardApplication.NOTIFICATION_ID, notification);
        }
    }

    private void hideNotification() {
        if (Build.VERSION.SDK_INT >= 5) {
            stopForeground(true);
        }
        else {
            NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NM.cancel(ClipboardApplication.NOTIFICATION_ID);
        }
    }
}
