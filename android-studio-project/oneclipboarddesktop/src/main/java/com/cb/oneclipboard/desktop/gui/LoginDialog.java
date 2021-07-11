package com.cb.oneclipboard.desktop.gui;

import com.cb.oneclipboard.desktop.ApplicationConstants.Property;
import com.cb.oneclipboard.desktop.Client;
import com.cb.oneclipboard.desktop.ClientPreferences;
import com.cb.oneclipboard.desktop.Utilities;
import com.cb.oneclipboard.desktop.model.Credentials;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginDialog extends JDialog {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JTextField serverHostField;
    private JTextField serverPortField;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private ClientPreferences prefs = new ClientPreferences();

    public LoginDialog() {
        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        String strVal;

        setModal(true);
        setIconImage(UIUtility.createImage("/images/logo.png", "OneClipboard"));
        setTitle(Utilities.getFullApplicationName());
        getContentPane().setBackground(Color.BLACK);
        setBackground(Color.BLACK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblEnterAnyUsernamepassword = new JLabel(
                "<html>Enter any username/password pair,<br>and enter the same combination on your other devices.</html>");
        lblEnterAnyUsernamepassword.setAlignmentY(Component.TOP_ALIGNMENT);
        lblEnterAnyUsernamepassword.setForeground(Color.WHITE);
        lblEnterAnyUsernamepassword.setBounds(10, -10, 365, 65);
        getContentPane().add(lblEnterAnyUsernamepassword);

        JLabel lblServerHost = new JLabel("Server Host");
        lblServerHost.setForeground(Color.WHITE);
        lblServerHost.setBounds(10, 60, 75, 25);
        getContentPane().add(lblServerHost);

        serverHostField = new JTextField();
        serverHostField.setBounds(90, 60, 225, 25);
        getContentPane().add(serverHostField);
        serverHostField.setColumns(10);

        strVal = prefs.getHost();
        serverHostField.setText(strVal);

        JLabel lblServerPort = new JLabel("Server Port");
        lblServerPort.setForeground(Color.WHITE);
        lblServerPort.setBounds(10, 90, 75, 25);
        getContentPane().add(lblServerPort);

        serverPortField = new JTextField();
        serverPortField.setBounds(90, 90, 225, 25);
        getContentPane().add(serverPortField);
        serverPortField.setColumns(10);

        strVal = Integer.toString(prefs.getPort(), 10);
        serverPortField.setText(strVal);

        JLabel lblUserName = new JLabel("Username");
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setBounds(10, 120, 75, 25);
        getContentPane().add(lblUserName);

        userNameField = new JTextField();
        userNameField.setBounds(90, 120, 225, 25);
        getContentPane().add(userNameField);
        userNameField.setColumns(10);

        strVal = prefs.getUsername();
        if (strVal != null) userNameField.setText(strVal);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(10, 150, 75, 25);
        getContentPane().add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setColumns(10);
        passwordField.setBounds(90, 150, 225, 25);
        getContentPane().add(passwordField);

        strVal = prefs.getPassword();
        if (strVal != null) passwordField.setText(strVal);

        final JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String serverHost = serverHostField.getText().trim();
                String serverPort = serverPortField.getText().trim();
                String userName   = userNameField.getText().trim();
                String password   = String.valueOf(passwordField.getPassword()).trim();
                if (serverHost.length() == 0 || serverPort.length() == 0 || userName.length() == 0 || password.length() == 0) {
                    return;
                }
                int port;
                try {
                    port = Integer.parseInt(serverPort, 10);
                }
                catch(Exception error) {
                    return;
                }
                LoginDialog.this.dispose();
                Client.propertyChangeSupport.firePropertyChange(Property.LOGIN, null, new Credentials(serverHost, port, userName, password));
            }
        });
        btnLogin.setBounds(90, 180, 225, 25);

        passwordField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }

            }
        });

        getContentPane().add(btnLogin);
        setResizable(false);
        setPreferredSize(new Dimension(335, 250));
        setSize(335, 250);
        setLocationRelativeTo(null);
        pack();
    }
}
