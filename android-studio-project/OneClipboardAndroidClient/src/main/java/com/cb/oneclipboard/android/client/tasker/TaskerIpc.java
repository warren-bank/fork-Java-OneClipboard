package com.cb.oneclipboard.android.client.tasker;

import com.cb.oneclipboard.android.client.R;
import com.cb.oneclipboard.android.client.tasker.ui.EditActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

/*
 * -------------------------------------------------------------------
 * This class provides static methods to manage Tasker inter-process communication at a high-level.
 *
 * 1) When the Tasker user creates a profile that responds to a OneClipboard event,
 *    ".tasker.ui.EditActivity" is started for a result.
 *
 * 2) ".tasker.ui.EditActivity" allows the user to decide whether to fire the event when:
 *      - the clipboard is updated remotely (with text received from the server)
 *      - the clipboard is updated locally  (with text copied by the user, and pushed to the server)
 *      - either of the above
 *
 * 3) ".tasker.ui.EditActivity" returns a Bundle to Tasker that contains the user's selection.
 *
 * 4) When OneClipboard performs an action, it broadcasts an Intent to notify Tasker:
 *        ACTION_REQUEST_QUERY
 *
 *    This Intent includes a "pass-through" data Bundle that contains:
 *      - the new clipboard text
 *      - the origin (ie: remote vs. local)
 *
 * 5) Tasker responds to this Intent by broadcasting an Intent in response:
 *        QUERY_CONDITION
 *
 * 6) ".tasker.receiver.QueryReceiver" receives the response.
 *
 *    This Intent includes:
 *      - the "configuration" data Bundle
 *      - the "pass-through"  data Bundle
 *    The receiver can compare:
 *      - the origin value(s) that should trigger a Tasker event
 *      - the origin value that actually occurred
 *    If the Tasker event should fire:
 *      - the Intent returned to Tasker contains:
 *        * code: RESULT_CONDITION_SATISFIED
 *        * an optional Bundle that contains values,
 *          which Tasker will assign to local variables
 *    If the Tasker event should NOT fire:
 *      - the Intent returned to Tasker contains:
 *        * code: RESULT_CONDITION_UNSATISFIED
 *
 * -------------------------------------------------------------------
 */

public final class TaskerIpc {

  // -----------------------------------------------------------------
  // EditActivity
  // -----------------------------------------------------------------

  private static final String CONFIG_BUNDLE_INT_EVENT_ORIGIN = "EVENT_ORIGIN";

  private static int getConfigValueForRadioButtonId(int resId) {
    switch(resId) {
      case R.id.tasker_config_radio_origin_remote:
        return 1;
      case R.id.tasker_config_radio_origin_local:
        return 2;
      case R.id.tasker_config_radio_origin_any:
      default:
        return 3;
    }
  }

  private static int getRadioButtonIdForConfigValue(int val) {
    switch(val) {
      case 1:
        return R.id.tasker_config_radio_origin_remote;
      case 2:
        return R.id.tasker_config_radio_origin_local;
      case 3:
      default:
        return R.id.tasker_config_radio_origin_any;
    }
  }

  private static void setCheckedRadioButtonFromConfigBundle(RadioGroup rg, Bundle configBundle) {
    if (rg == null || configBundle == null || !configBundle.containsKey(CONFIG_BUNDLE_INT_EVENT_ORIGIN)) return;

    int resId = getRadioButtonIdForConfigValue(
      configBundle.getInt(CONFIG_BUNDLE_INT_EVENT_ORIGIN)
    );

    rg.check(resId);
  }

  public static void setCheckedRadioButtonFromConfigIntent(RadioGroup rg, Intent intent) {
    Bundle configBundle = intent.getBundleExtra(Locale.Intent.EXTRA_BUNDLE);

    setCheckedRadioButtonFromConfigBundle(rg, configBundle);
  }

  private static Bundle getConfigBundleFromRadioGroup(RadioGroup rg) {
    int resId = rg.getCheckedRadioButtonId();

    Bundle configBundle = new Bundle();
    configBundle.putInt(
      CONFIG_BUNDLE_INT_EVENT_ORIGIN,
      getConfigValueForRadioButtonId(resId)
    );

    return configBundle;
  }

  private static String getBlurb(Context context, Bundle configBundle) {
    switch(configBundle.getInt(CONFIG_BUNDLE_INT_EVENT_ORIGIN)) {
      case 1:
        return context.getString(R.string.tasker_blurb_origin_remote);
      case 2:
        return context.getString(R.string.tasker_blurb_origin_local);
      case 3:
      default:
        return context.getString(R.string.tasker_blurb_origin_any);
    }
  }

  public static Intent getResultIntentFromRadioGroup(Context context, RadioGroup rg) {
    final Bundle configBundle = getConfigBundleFromRadioGroup(rg);
    final String blurb        = getBlurb(context, configBundle);
    final Intent resultIntent = new Intent();

    resultIntent.putExtra(Locale.Intent.EXTRA_BUNDLE, configBundle);
    resultIntent.putExtra(Locale.Intent.EXTRA_STRING_BLURB, blurb);

    return resultIntent;
  }

  // -----------------------------------------------------------------
  // ClipboardApplication
  // -----------------------------------------------------------------

  private static final String DATA_BUNDLE_INT_EVENT_ORIGIN = "EVENT_ORIGIN";
  private static final String DATA_BUNDLE_STRING_CLIPBOARD = "CLIPBOARD";

  private static Intent getRequestQueryIntent(Bundle dataBundle) {
    Intent requestQueryIntent = new Intent(
      Locale.Intent.ACTION_REQUEST_QUERY
    );

    requestQueryIntent.putExtra(
      Locale.Intent.EXTRA_ACTIVITY,
      EditActivity.class.getName()
    );

    TaskerPlugin.Event.addPassThroughMessageID(requestQueryIntent);

    if (dataBundle != null) {
      TaskerPlugin.Event.addPassThroughData(requestQueryIntent, dataBundle);
    }

    return requestQueryIntent;
  }

  private static int getDataValueForEventOrigin(boolean updatedRemotelyByServer) {
    return updatedRemotelyByServer ? 1 : 2;
  }

  public static Intent getRequestQueryIntent(String clipboard, boolean updatedRemotelyByServer) {
    Bundle dataBundle = new Bundle();

    dataBundle.putInt(
      DATA_BUNDLE_INT_EVENT_ORIGIN,
      getDataValueForEventOrigin(updatedRemotelyByServer)
    );

    dataBundle.putString(
      DATA_BUNDLE_STRING_CLIPBOARD,
      clipboard
    );

    return getRequestQueryIntent(dataBundle);
  }

  // -----------------------------------------------------------------
  // QueryReceiver
  // -----------------------------------------------------------------

  public static boolean shouldQueryReceiverSendResult(Intent intent) {
    if (!Locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction()))
      return false;

    final Bundle bundle = intent.getBundleExtra(Locale.Intent.EXTRA_BUNDLE);

    if (bundle == null)
      return false;

    if (!bundle.containsKey(CONFIG_BUNDLE_INT_EVENT_ORIGIN))
      return false;

    return true;
  }

  public static int getQueryReceiverResultCode(Intent intent, Bundle resultExtras) {
    final Bundle bundle = intent.getBundleExtra(Locale.Intent.EXTRA_BUNDLE);
    final int messageID = TaskerPlugin.Event.retrievePassThroughMessageID(intent);

    if (messageID == -1)
      return Locale.Intent.RESULT_CONDITION_UNKNOWN;

    final Bundle dataBundle = TaskerPlugin.Event.retrievePassThroughData(intent);

    if (dataBundle == null || !dataBundle.containsKey(DATA_BUNDLE_INT_EVENT_ORIGIN) || !dataBundle.containsKey(DATA_BUNDLE_STRING_CLIPBOARD))
      return Locale.Intent.RESULT_CONDITION_UNKNOWN;

    int eventOriginConfig = bundle.getInt(CONFIG_BUNDLE_INT_EVENT_ORIGIN);
    int eventOriginData   = dataBundle.getInt(DATA_BUNDLE_INT_EVENT_ORIGIN);

    if (eventOriginConfig == 3 || eventOriginConfig == eventOriginData) {
      Bundle varsBundle = new Bundle();

      varsBundle.putString(
        "%clip",
        dataBundle.getString(DATA_BUNDLE_STRING_CLIPBOARD)
      );

      TaskerPlugin.addVariableBundle(resultExtras, varsBundle);

      return Locale.Intent.RESULT_CONDITION_SATISFIED;
    }
    else {
      return Locale.Intent.RESULT_CONDITION_UNSATISFIED;
    }
  }

}
