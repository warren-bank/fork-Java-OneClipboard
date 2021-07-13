package com.cb.oneclipboard.android.server.service;

import com.cb.oneclipboard.android.server.R;
import com.cb.oneclipboard.android.server.utils.ResourceUtils;
import com.cb.oneclipboard.android.server.networking.Server;

import android.os.Process;

public class NetworkingService extends BaseNetworkingService {

  @Override
  public void onCreate() {
    super.onCreate();

    Server.init(getPortNumber(), new Server.Callback(){
      @Override
      public void stopped() {
        stopInstance();
        Process.killProcess(Process.myPid());
      }
    });

    Server.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    Server.stop();
  }

  // -------------------------------------------------------------------------
  // abstract methods

  protected int getIcon() {
    return R.drawable.logo;
  }

  protected int getPortNumber() {
    return ResourceUtils.getInteger(getInstance(), R.integer.server_port);
  }

  protected String getPort() {
    return Integer.toString(getPortNumber(), 10);
  }

  protected Class getExportedServiceClass() {
    return NetworkingService.class;
  }
}
