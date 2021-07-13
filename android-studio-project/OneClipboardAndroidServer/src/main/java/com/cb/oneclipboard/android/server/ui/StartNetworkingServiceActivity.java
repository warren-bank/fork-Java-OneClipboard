package com.cb.oneclipboard.android.server.ui;

import com.cb.oneclipboard.android.server.MainApp;
import com.cb.oneclipboard.android.server.service.NetworkingService;
import com.cb.oneclipboard.android.server.utils.NetworkUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class StartNetworkingServiceActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    finish();

    if (!NetworkUtils.isWifiConnected(MainApp.getInstance())) {
      return;
    }

    startNetworkingService();
  }

  private void startNetworkingService() {
    Intent intent = new Intent(getApplicationContext(), NetworkingService.class);
    MainApp.getInstance().startService(intent);
  }
}
