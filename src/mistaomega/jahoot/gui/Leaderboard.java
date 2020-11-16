package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.ClientHandler;

import javax.swing.*;
import java.util.Map;

public class Leaderboard {
    private JList<String> scoresList;
    private JPanel mainPanel;
    private JTextField LEADERBOARDSTextField;
    private JFrame frame;


    /**
     * Entry function for the UI
     */
    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Leaderboard");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setSize(1000, 1000);
            frame.setVisible(false);
        });
    }

    public void displayLatestScores(Map<String, Integer> clientScores) {
        if (scoresList.getModel().getSize() == 0) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            scoresList.setModel(listModel);
        }
        DefaultListModel<String> listModel = (DefaultListModel<String>) scoresList.getModel();
        listModel.removeAllElements();


        for (String username :
                clientScores.keySet()) {
            listModel.addElement(String.format("%5s %-8s", username, clientScores.get(username)));
        }
    }

    public void show(){
        frame.setVisible(true);
    }

    public void hide(){
        frame.setVisible(false);
    }
}
