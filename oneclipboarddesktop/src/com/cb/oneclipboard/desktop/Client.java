package com.cb.oneclipboard.desktop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.cb.oneclipboard.desktop.gui.Tray;
import com.cb.oneclipboard.lib.ApplicationProperties;
import com.cb.oneclipboard.lib.Callback;
import com.cb.oneclipboard.lib.DefaultPropertyLoader;
import com.cb.oneclipboard.lib.Message;
import com.cb.oneclipboard.lib.MessageType;
import com.cb.oneclipboard.lib.SocketListener;
import com.cb.oneclipboard.lib.User;
import com.cb.oneclipboard.lib.socket.ClipboardConnector;
import com.jezhumble.javasysmon.JavaSysMon;
import com.sun.org.apache.bcel.internal.generic.LCONST;

public class Client {

	private static String serverAddress = null;
	private static int serverPort;
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static User user = null;

	public static final String[] PROP_LIST = { "config.properties" };

	public static void main(String[] args) {
		final TextTransfer textTransfer = new TextTransfer();
		
		init(args);

		// Poll the clipboard for changes
		final ClipboardPollTask clipboardPollTask = new ClipboardPollTask(textTransfer, new Callback() {

			@Override
			public void execute(Object object) {
				String clipboardText = (String) object;
				ClipboardConnector.send(new Message(clipboardText, user));
			}
		});

		// Listen for clipboard content from other clients
		ClipboardConnector.connect(serverAddress, serverPort, user, new SocketListener() {

			@Override
			public void onMessageReceived(Message message) {
				clipboardPollTask.updateClipboardContent(message.getText());
				textTransfer.setClipboardContents(message.getText());
			}

			@Override
			public void onConnect() {
				ClipboardConnector.send(new Message("register", MessageType.REGISTER, user));
			}

		});

		// Run the poll task every 2 seconds
		final ScheduledFuture<?> pollHandle = scheduler.scheduleAtFixedRate(clipboardPollTask, 1, 2, TimeUnit.SECONDS);

	}

	public static void init(String args[]) {
		pipeSysoutToFile();
		
		// Initialize properties
		ApplicationProperties.loadProperties(PROP_LIST, new DefaultPropertyLoader());
		serverAddress = ApplicationProperties.getStringProperty("server");
		serverPort = ApplicationProperties.getIntProperty("server_port");

		if (args.length > 0) {
			try {
				serverPort = Integer.parseInt(args[0]);
			} catch (Exception e) {
			}
		}
		
		Tray.init(args);
		
		// Set user
		user = new User("testuser", "testpass");
	}
	
	static String lockFileName = System.getProperty("user.home") + File.separator + "oneclipboard.lock";
	
	public static void lock() throws Exception {
		JavaSysMon monitor =   new JavaSysMon();
		int pid = monitor.currentPid();
		
		FileOutputStream fos = new FileOutputStream(lockFileName);
		fos.write(String.valueOf(pid).getBytes());
	}
	
	public static void isRunning(){
		File lockFile = new File(lockFileName);
		if(lockFile.exists()){
			
		}
	}
	
	private static int getPID(File lockFile){
		int pid = -1;
		// Read PID
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(lockFile));
			pid = Integer.parseInt(reader.readLine().trim());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return pid;
	}

	private static void pipeSysoutToFile() {
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
