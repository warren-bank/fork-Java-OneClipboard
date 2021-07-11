package com.cb.oneclipboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.cb.oneclipboard.lib.CipherManager;
import com.cb.oneclipboard.lib.User;
import com.cb.oneclipboard.lib.Util;
import com.cb.oneclipboard.util.IntentUtil;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ClipboardApplication app = ((ClipboardApplication) getApplicationContext());

        if (!OneClipboardService.isRunning) {
            setContentView(R.layout.login);

            TextView serverHostField = (TextView) findViewById(R.id.serverHostField);
            TextView serverPortField = (TextView) findViewById(R.id.serverPortField);
            TextView usernameField   = (TextView) findViewById(R.id.usernameField);
            TextView passwordField   = (TextView) findViewById(R.id.passwordField);

            if (app.pref.getHost() != null)
                serverHostField.setText(app.pref.getHost());
            else
                serverHostField.setText(getString(R.string.server_host));

            if (app.pref.getPort() != null)
                serverPortField.setText(app.pref.getPort());
            else
                serverPortField.setText(getString(R.string.server_port));

            if (app.pref.getUsername() != null)
                usernameField.setText(app.pref.getUsername());

            if (app.pref.getPassword() != null)
                passwordField.setText(app.pref.getPassword());

            Button loginButton = (Button) findViewById(R.id.btnLogin);
            loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    String host     = serverHostField.getText().toString().trim();
                    String port     = serverPortField.getText().toString().trim();
                    String username = usernameField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();

                    int port_number;
                    try {
                        port_number = Integer.parseInt(port, 10);
                    }
                    catch(Exception e) {
                        port_number = -1;
                    }

                    if (host.length() > 0 && port_number > 0 && username.length() > 0 && password.length() > 0) {
                        app.pref.setHost(host);
                        app.pref.setPort(port);
                        app.pref.setUsername(username);
                        app.pref.setPassword(password);
                        initAndStart();
                    }
                }

            });
        } else {
            startActivity(IntentUtil.getHomePageIntent(this));
            finish();
        }
    }

    public void initAndStart() {
        try {
            ClipboardApplication app = ((ClipboardApplication) getApplicationContext());
            CipherManager cipherManager = new CipherManager(app.pref.getUsername(), app.pref.getPassword());
            String sha256Hash = Util.getSha256Hash(cipherManager.getEncryptionPassword());
            User user = new User(app.pref.getUsername(), sha256Hash);
            app.setUser(user);
            app.setCipherManager(cipherManager);

            Intent oneclipboardServiceIntent = new Intent(MainActivity.this, OneClipboardService.class);
            startService(oneclipboardServiceIntent);

            Toast.makeText(this, getString(R.string.app_name) + " has started!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to initialize " + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
