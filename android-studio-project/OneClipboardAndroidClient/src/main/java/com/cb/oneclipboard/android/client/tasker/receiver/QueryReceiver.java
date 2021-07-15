package com.cb.oneclipboard.android.client.tasker.receiver;

import com.cb.oneclipboard.android.client.tasker.TaskerIpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public final class QueryReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(final Context context, final Intent intent) {
    if (!TaskerIpc.shouldQueryReceiverSendResult(intent))
      return;

    Bundle resultExtras = getResultExtras(true);

    setResultCode(
      TaskerIpc.getQueryReceiverResultCode(intent, resultExtras)
    );
  }

}
