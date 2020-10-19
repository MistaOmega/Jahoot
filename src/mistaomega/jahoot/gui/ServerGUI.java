package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.JahootServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI {
    private JList Users;
    private JPanel mainPanel;
    private JButton ready;
    private static JahootServer jahootServer;

    public ServerGUI() {
        ready.addActionListener(e -> jahootServer.setReadyToPlay(true));
    }

    public static void main(String[] args){
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
