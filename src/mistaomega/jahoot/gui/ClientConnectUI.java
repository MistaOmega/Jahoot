package mistaomega.jahoot.gui;

import mistaomega.jahoot.client.Client;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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


    public ClientConnectUI() {

        tfHostnameDefault = tfHostname.getText();
        tfPortDefault = tfPort.getText();
        tfUsernameDefault = tfUsername.getText();

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

                try{
                    Integer.parseInt(tfPort.getText());
                } catch (NumberFormatException numberFormatException) {
                    tfPort.setText(tfPortDefault);
                }
            }
        });
        btnConnect.addActionListener(e -> {
            int port;
            try{
                port = Integer.parseInt(tfPort.getText());
            } catch (NumberFormatException numberFormatException) {
                tfPort.setText(tfPortDefault);
                return;
            }
            Client client = new Client(tfHostname.getText(), port, tfUsername.getText());
            client.run();
        });
    }

    public static void main(String[] args) {
        ClientConnectUI clientConnectUI = new ClientConnectUI();
        clientConnectUI.run();
    }

    public void run() {
        JFrame frame = new JFrame("Connect GUI");
        frame.setContentPane(new ClientConnectUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
