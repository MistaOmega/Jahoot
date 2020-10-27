package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.JahootServer;

import javax.swing.*;
import java.awt.*;

public class ServerGUI {
    private static JahootServer jahootServer;
    private JList Users;
    private JPanel mainPanel;
    private JButton ready;

    public ServerGUI() {
        ready.addActionListener(e -> jahootServer.setReadyToPlay(true));
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Server GUI");
            frame.setContentPane(new ServerGUI().mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
        jahootServer = new JahootServer(5000);
        jahootServer.run();
    }


}
