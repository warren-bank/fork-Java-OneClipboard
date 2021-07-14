package com.cb.oneclipboard.android.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(ClipboardApplication.CLIPBOARD_UPDATED));

        // This is necessary as the textView won't be updated when the activity wasn't visible.
        final ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        TextView clipboardTextView = (TextView) findViewById(R.id.homePageText);
        clipboardTextView.setText(clipBoard.getText());
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
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
