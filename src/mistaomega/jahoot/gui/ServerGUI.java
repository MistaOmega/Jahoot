package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.JahootServer;

import javax.swing.*;

public class ServerGUI {
    private static JahootServer jahootServer;
    private JList Users;
    private JPanel mainPanel;
    private JButton ready;

    private boolean ReadyToPlay = false;

    public ServerGUI() {
        ready.addActionListener(e -> setReadyToPlay());
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

    public boolean isReadyToPlay() {
        return ReadyToPlay;
    }

    public void setReadyToPlay() {
        jahootServer.setReadyToPlay(true);
    }
}
