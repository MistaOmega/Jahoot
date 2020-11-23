package mistaomega.jahoot.gui;

import mistaomega.jahoot.client.Client;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;

/**
 * This class is the controller class for the ClientConnectUI Form
 *
 * @author Jack Nash
 * @version 1.0
 */
public class ClientConnectUI extends UserInterfaceControllerClass {
    private final String tfUsernameDefault;
    private final String tfPortDefault;
    private final String tfHostnameDefault;
    private JPanel mainPanel;
    private JButton btnConnect;
    private JTextField tfUsername;
    private JTextField tfHostname;
    private JTextField tfPort;
    private JTextArea consoleOutput;


    public ClientConnectUI() {
        super(new JFrame("Connect GUI"));
        tfHostnameDefault = tfHostname.getText();
        tfPortDefault = tfPort.getText();
        tfUsernameDefault = tfUsername.getText();

        initListeners();
    }

    /**
     * listeners set here
     */
    public void initListeners() {
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

    public void run() {
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack(); // sets minimum scale for UI, so shrinking from setSize doesn't cause elements to disappear
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    public void setConsoleOutput(String message) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        String timestamp = String.format("[%02d:%02d:%02d] ", hour, minute, second);
        consoleOutput.append(timestamp + message + "\n");
    }


    public JButton getBtnConnect() {
        return btnConnect;
    }

}
