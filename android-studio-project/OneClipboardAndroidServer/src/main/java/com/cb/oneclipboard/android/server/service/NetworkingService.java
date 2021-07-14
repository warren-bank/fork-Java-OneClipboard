package com.cb.oneclipboard.android.server.service;

import com.cb.oneclipboard.android.server.R;
import com.cb.oneclipboard.android.server.utils.ResourceUtils;
import com.cb.oneclipboard.android.server.networking.NetworkingServer;

import android.os.Process;

public class NetworkingService extends BaseNetworkingService {

  @Override
  public void onCreate() {
    super.onCreate();

    NetworkingServer.init(getPortNumber(), new NetworkingServer.Callback(){
      @Override
      public void stopped() {
        stopInstance();
        Process.killProcess(Process.myPid());
      }
    });

    NetworkingServer.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    NetworkingServer.stop();
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
