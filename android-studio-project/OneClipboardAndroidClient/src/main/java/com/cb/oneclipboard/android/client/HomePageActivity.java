package com.cb.oneclipboard.android.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class HomePageActivity extends Activity {

    private static final String TAG = "HomePageActivity";

    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String clipboardText = intent.getStringExtra("message");
                TextView clipboardTextView = (TextView) findViewById(R.id.homePageText);
                clipboardTextView.setText(clipboardText);

                // Explicitly setting height as otherwise the textView doesn't fill screen height for long text.
                LayoutParams params = clipboardTextView.getLayoutParams();
                params.height = getResources().getDimensionPixelSize(R.dimen.text_view_height);
                clipboardTextView.setLayoutParams(params);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(ClipboardApplication.CLIPBOARD_UPDATED));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) return;

        final ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        final CharSequence     clipText  = clipBoard.getText();

        if ((clipText != null) && (clipText.length() > 0)) {
            // This is necessary because the textView won't be updated when the activity wasn't visible.
            TextView clipboardTextView = (TextView) findViewById(R.id.homePageText);
            clipboardTextView.setText(clipText);

            // [Android 10+] This is necessary because ClipboardApplication.clipboardListener was NOT allowed to access this text when the activity wasn't visible.
            if (Build.VERSION.SDK_INT >= 29) {
                clipBoard.setText(clipText);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ClipboardApplication app = (ClipboardApplication) getApplication();

        switch (item.getItemId()) {
            case R.id.menu_reconnect:
                if (!app.isConnected()) {
                    app.establishConnection();
                }
                return true;
            case R.id.menu_logout:
                stopService();
                finish();
                app.pref.clear();
                Intent login = new Intent(this, MainActivity.class);
                startActivity(login);
                return true;
            case R.id.menu_quit:
                stopService();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void stopService() {
        Intent oneclipboardServiceIntent = new Intent(this, OneClipboardService.class);
        stopService(oneclipboardServiceIntent);
    }
}
