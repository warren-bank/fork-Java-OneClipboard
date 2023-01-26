package com.cb.oneclipboard.android.server.ui;

import com.cb.oneclipboard.android.server.MainApp;
import com.cb.oneclipboard.android.server.service.NetworkingService;
import com.cb.oneclipboard.android.server.utils.NetworkUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

public class StartNetworkingServiceActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestPermissions();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    requestPermissions();
  }

  private void requestPermissions() {
    boolean OK = true;

    if (Build.VERSION.SDK_INT >= 33) {
      if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        OK = false;
        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
      }
    }

    if (OK) {
      if (NetworkUtils.isWifiConnected(MainApp.getInstance()))
        startNetworkingService();

      finish();
    }
  }

  private void startNetworkingService() {
    Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
    MainApp.getInstance().startService(intent);
  }
}
