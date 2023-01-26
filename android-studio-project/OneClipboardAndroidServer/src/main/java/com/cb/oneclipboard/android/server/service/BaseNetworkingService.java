package com.cb.oneclipboard.android.server.service;

import com.cb.oneclipboard.android.server.R;
import com.cb.oneclipboard.android.server.utils.NetworkUtils;
import com.cb.oneclipboard.android.server.utils.ResourceUtils;
import com.cb.oneclipboard.android.server.utils.WakeLockMgr;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.net.InetAddress;

public abstract class BaseNetworkingService extends Service {
  private static final String ACTION_STOP = "STOP";

  private static BaseNetworkingService instance;

  private InetAddress localAddress;

  @Override
  public void onCreate() {
    super.onCreate();

    instance = BaseNetworkingService.this;
    WakeLockMgr.acquire(/* context= */ instance);
    showNotification();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    onStart(intent, startId);
    return START_STICKY;
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);

    processIntent(intent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    instance = null;
    WakeLockMgr.release();
    hideNotification();
  }

  public static BaseNetworkingService getInstance() {
    return instance;
  }

  public static void stopInstance() {
    if (instance != null) {
      instance.stopSelf();
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  // -------------------------------------------------------------------------
  // foregrounding..

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

  private int getNotificationId() {
    return ResourceUtils.getInteger(instance, R.integer.NOTIFICATION_ID_NETWORKING_SERVICE);
  }

  private void showNotification() {
    Notification notification = getNotification();
    int NOTIFICATION_ID = getNotificationId();

    if (Build.VERSION.SDK_INT >= 5) {
      createNotificationChannel();
      startForeground(NOTIFICATION_ID, notification);
    }
    else {
      NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      NM.notify(NOTIFICATION_ID, notification);
    }
  }

  private void hideNotification() {
    if (Build.VERSION.SDK_INT >= 5) {
      stopForeground(true);
    }
    else {
      NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      int NOTIFICATION_ID    = getNotificationId();
      NM.cancel(NOTIFICATION_ID);
    }
  }

  private Notification getNotification() {
    Notification notification;

    if (Build.VERSION.SDK_INT >= 26) {
      Notification.Builder builder = new Notification.Builder(/* context= */ instance, /* channelId= */ getNotificationChannelId());

      if (Build.VERSION.SDK_INT >= 31) {
        builder.setContentTitle(getNetworkAddress());
        builder.setContentText (getString(R.string.notification_service_content_line3));
        builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
      }

      notification = builder.build();
    }
    else {
      notification = new Notification();
    }

    notification.when          = System.currentTimeMillis();
    notification.flags         = 0;
    notification.flags        |= Notification.FLAG_ONGOING_EVENT;
    notification.flags        |= Notification.FLAG_NO_CLEAR;
    notification.icon          = getIcon();
    notification.tickerText    = getString(R.string.notification_service_ticker);
    notification.contentIntent = getPendingIntent_StopService();
    notification.deleteIntent  = getPendingIntent_StopService();

    if (Build.VERSION.SDK_INT >= 16) {
      notification.priority    = Notification.PRIORITY_HIGH;
    }
    else {
      notification.flags      |= Notification.FLAG_HIGH_PRIORITY;
    }

    if (Build.VERSION.SDK_INT >= 21) {
      notification.visibility  = Notification.VISIBILITY_PUBLIC;
    }

    RemoteViews contentView    = new RemoteViews(getPackageName(), R.layout.service_notification);
    contentView.setImageViewResource(R.id.notification_icon,  getIcon());
    contentView.setTextViewText(R.id.notification_text_line1, getString(R.string.notification_service_content_line1));
    contentView.setTextViewText(R.id.notification_text_line2, getNetworkAddress());
    contentView.setTextViewText(R.id.notification_text_line3, getString(R.string.notification_service_content_line3));

    if (Build.VERSION.SDK_INT < 31)
      notification.contentView = contentView;
    if (Build.VERSION.SDK_INT >= 16)
      notification.bigContentView = contentView;
    if (Build.VERSION.SDK_INT >= 21)
      notification.headsUpContentView = contentView;

    return notification;
  }

  private PendingIntent getPendingIntent_StopService() {
    Intent intent = new Intent(instance, getExportedServiceClass());
    intent.setAction(ACTION_STOP);

    int flags = PendingIntent.FLAG_UPDATE_CURRENT;
    if (Build.VERSION.SDK_INT >= 23)
      flags |= PendingIntent.FLAG_IMMUTABLE;

    return PendingIntent.getService(instance, 0, intent, flags);
  }

  private String getNetworkAddress() {
    if (localAddress == null)
      localAddress = NetworkUtils.getLocalIpAddress(); //Get local IP object

    return (localAddress == null)
      ? "[offline]"
      : localAddress.getHostAddress() + ":" + getPort();
  }

  // -------------------------------------------------------------------------
  // process inbound intents

  private void processIntent(Intent intent) {
    if (intent == null) return;

    String action = intent.getAction();

    if ((action != null) && action.equals(ACTION_STOP))
      instance.stopSelf();
  }

  // -------------------------------------------------------------------------
  // abstract methods

  protected abstract int    getIcon();
  protected abstract int    getPortNumber();
  protected abstract String getPort();
  protected abstract Class  getExportedServiceClass();
}
