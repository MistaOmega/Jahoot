package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.JahootServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI {
    private static JahootServer jahootServer;
    private JList Users;
    private JPanel mainPanel;
    private JButton ready;

    private boolean readytoplay = false;

    public ServerGUI() {
        ready.addActionListener(e -> jahootServer.setReadyToPlay(true));
        ready.addActionListener(e -> {
            readytoplay = true;
        });
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Server GUI");
            frame.setContentPane(new ServerGUI().mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
        jahootServer = new JahootServer(5000, this);
        jahootServer.run();
    }

    public boolean isReadytoplay() {
        return readytoplay;
    }
}
