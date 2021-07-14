package com.cb.oneclipboard.desktop.client;

import com.cb.oneclipboard.desktop.client.ApplicationConstants.Property;
import com.cb.oneclipboard.desktop.client.gui.ApplicationUI;
import com.cb.oneclipboard.desktop.client.model.Credentials;

import com.cb.oneclipboard.lib.common.*;
import com.cb.oneclipboard.lib.client.*;
import com.cb.oneclipboard.lib.client.socket.*;
import com.cb.oneclipboard.lib.desktop.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class Client implements PropertyChangeListener {
    private static final String[] PROP_LIST = {"config.properties"};
    private static final String lockFileName = System.getProperty("user.home") + File.separator + "oneclipboard.lock";
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final ApplicationUI ui = new ApplicationUI();

    private static Client client = null;
    private static ScheduledExecutorService scheduler = null;
    private static CipherManager cipherManager = null;
    private static User user = null;
    private ClipboardConnector clipboardConnector = null;
    private ClientPreferences prefs = new ClientPreferences();

    public static ApplicationPropertyChangeSupport propertyChangeSupport = new ApplicationPropertyChangeSupport();

    public static void main(String[] args) {
        client = new Client();
        client.init();
    }

    public void init() {
        try {
            SingleInstance.lock(lockFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize properties
        ApplicationProperties.loadProperties(PROP_LIST, new DefaultPropertyLoader());
        propertyChangeSupport.addPropertyChangeListener(this);

        ui.showLogin();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                stop();
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Property property = Property.valueOf(propertyName);

        switch (property) {
            case LOGIN:
                Credentials credentials = (Credentials) evt.getNewValue();
                prefs.setHost(credentials.getHost());
                prefs.setPort(credentials.getPort());
                prefs.setUsername(credentials.getUserName());
                prefs.setPassword(credentials.getPassword());
                setupAndStart();
                break;
            case START:
                client.start();
                break;
            case STOP:
                client.stop();
                break;
            case LOGOUT:
                prefs.clear();
                client.stop();
                ui.showLogin();
        }
    }

    private void setupAndStart() {
        try {
            cipherManager = new CipherManager(prefs.getUsername(), prefs.getPassword());
            String sha256Hash = Util.getSha256Hash(cipherManager.getEncryptionPassword());
            user = new User(prefs.getUsername(), sha256Hash);
            client.start();
            ui.createAndShowTray();
        } catch (Exception e) {
            final String errorMsg = "Unable to initialize OneClipboard";
            LOGGER.log(Level.SEVERE, errorMsg, e);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ui.showErrorDialog(errorMsg);
                    System.exit(1);
                }
            });
        }
    }

    public void start() {
        try {
            final TextTransfer textTransfer = new TextTransfer();
            scheduler = Executors.newScheduledThreadPool(1);

            // Poll the clipboard for changes
            final ClipboardPollTask clipboardPollTask = new ClipboardPollTask(textTransfer, new Callback() {

                @Override
                public void execute(Object object) {
                    String clipboardText = (String) object;
                    send(new Message(cipherManager.encrypt(clipboardText), user));
                }
            });

            // Listen for clipboard content from other clients
            clipboardConnector = new ClipboardConnector()
                    .server(prefs.getHost())
                    .port(prefs.getPort())
                    .socketListener(new SocketListener() {

                        @Override
                        public void onMessageReceived(Message message) {
                            String clipboardText = cipherManager.decrypt(message.getText());
                            clipboardPollTask.updateClipboardContent(clipboardText);
                            textTransfer.setClipboardContents(clipboardText);
                        }

                        @Override
                        public void onConnect() {
                            send(new Message("register", MessageType.REGISTER, user));
                        }

                        @Override
                        public void onDisconnect() {
                            LOGGER.info("Lost connection to server");
                            stop();
                        }

                    })
                    .connect();
            // Run the poll task every 2 seconds
            final ScheduledFuture<?> pollHandle = scheduler.scheduleAtFixedRate(clipboardPollTask, 1, 2, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting client", e);
        } finally {
        }

    }

    public boolean isConnected() {
        return clipboardConnector != null && clipboardConnector.isConnected();
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        if (isConnected()) {
            send(new Message("disconnect", MessageType.DISCONNECT, user));
            clipboardConnector.close();
        }
    }

    private void send(Message message) {
        if (isConnected()) {
            clipboardConnector.send(message);
        }
    }

}
