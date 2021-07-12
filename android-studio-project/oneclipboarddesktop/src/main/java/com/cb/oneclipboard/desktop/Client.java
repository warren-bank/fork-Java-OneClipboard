package com.cb.oneclipboard.desktop;

import com.cb.oneclipboard.desktop.ApplicationConstants.Property;
import com.cb.oneclipboard.desktop.gui.ApplicationUI;
import com.cb.oneclipboard.desktop.model.Credentials;
import com.cb.oneclipboard.lib.*;
import com.cb.oneclipboard.lib.socket.ClipboardConnector;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.security.Security;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements PropertyChangeListener {
    public static final String[] PROP_LIST = {"config.properties"};
    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());
    public static ApplicationPropertyChangeSupport propertyChangeSupport = new ApplicationPropertyChangeSupport();
    static String lockFileName = System.getProperty("user.home") + File.separator + "oneclipboard.lock";
    private static Client client = null;
    private static ApplicationUI ui = new ApplicationUI();
    private static ScheduledExecutorService scheduler = null;
    private static CipherManager cipherManager = null;
    private static User user = null;
    private ClipboardConnector clipboardConnector = null;
    private ClientPreferences prefs = new ClientPreferences();

    public static void main(String[] args) {
        // Set logging file location
        String logFileLocation = System.getProperty("user.home") + File.separator + "onecliboarddesktop.log";
        System.setProperty("java.util.logging.config.file", logFileLocation);

        client = new Client();
        client.init();
    }

    public void init() {
        //pipeSysoutToFile();

        try {
            SingleInstance.lock(lockFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize properties
        ApplicationProperties.loadProperties(PROP_LIST, new DefaultPropertyLoader());
        propertyChangeSupport.addPropertyChangeListener(this);

        ui.showLogin();
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
                            // TODO Auto-generated method stub

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

    public void stop() {
        scheduler.shutdownNow();
        clipboardConnector.close();
    }

    private void send(Message message) {
        if (clipboardConnector != null) {
            clipboardConnector.send(message);
        }
    }

    private void pipeSysoutToFile() {
        try {
            String logFileName = System.getProperty("user.home") + File.separator + "oneclipboard.log";
            File file = new File(logFileName);
            PrintStream printStream = new PrintStream(new FileOutputStream(file));
            System.setOut(printStream);
            System.setErr(printStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
