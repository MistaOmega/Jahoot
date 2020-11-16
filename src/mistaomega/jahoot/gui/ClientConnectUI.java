package mistaomega.jahoot.gui;

import mistaomega.jahoot.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;

public class ClientConnectUI {
    private final String tfUsernameDefault;
    private final String tfPortDefault;
    private final String tfHostnameDefault;
    private JPanel mainPanel;
    private JTextField tfTitle;
    private JButton btnConnect;
    private JTextField tfUsername;
    private JTextField tfHostname;
    private JTextField tfPort;
    private JTextArea consoleOutput;
    private JFrame mainFrame;


    public ClientConnectUI() {
        tfHostnameDefault = tfHostname.getText();
        tfPortDefault = tfPort.getText();
        tfUsernameDefault = tfUsername.getText();

        initListeners();
    }

    /**
     * listeners set here
     */
    public void initListeners(){
        tfUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                tfUsername.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (tfUsername.getText().isEmpty()) {
                    tfUsername.setText(tfUsernameDefault);
                }
            }
        });
        tfHostname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                tfHostname.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (tfHostname.getText().isEmpty()) {
                    tfHostname.setText(tfHostnameDefault);
                }
            }
        });
        tfPort.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                tfPort.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (tfPort.getText().isEmpty()) {
                    tfPort.setText(tfPortDefault);
                    return;
                }

                try {
                    Integer.parseInt(tfPort.getText());
                } catch (NumberFormatException numberFormatException) {
                    tfPort.setText(tfPortDefault);
                }
            }
        });
        btnConnect.addActionListener(e -> {
            int port;
            try {
                port = Integer.parseInt(tfPort.getText());
            } catch (NumberFormatException numberFormatException) {
                setConsoleOutput("Port format incorrect");
                tfPort.setText(tfPortDefault);
                return;
            }
            btnConnect.setEnabled(false);

            new Thread(() -> {
                Client client = new Client(tfHostname.getText(), port, tfUsername.getText(), this);
                client.run();
            }).start();
        });
    }

    public static void main(String[] args) {
        ClientConnectUI clientConnectUI = new ClientConnectUI();
        clientConnectUI.run();
    }

    public void run() {
        mainFrame = new JFrame("Connect GUI");
        mainFrame.setContentPane(new ClientConnectUI().mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public void setConsoleOutput(String message) {
        consoleOutput.append(message + "\n");
    }

    public void clearConsole() {
        consoleOutput.setText("");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getBtnConnect() {
        return btnConnect;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
}
