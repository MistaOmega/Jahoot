package mistaomega.jahoot.gui;

import mistaomega.jahoot.lib.CommonUtils;
import mistaomega.jahoot.server.ClientHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
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

    public void displayLatestScores(Map<String, Integer> clientScores, boolean winning) {
        if (scoresList.getModel().getSize() == 0) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            scoresList.setModel(listModel);
        }
        DefaultListModel<String> listModel = (DefaultListModel<String>) scoresList.getModel();
        listModel.removeAllElements();


        DefaultListCellRenderer renderer = (DefaultListCellRenderer) scoresList.getCellRenderer(); //Centers text within a JList, found at: https://stackoverflow.com/a/21029692
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        int highScore = 0;
        for (String username :
                clientScores.keySet()) {
            if(winning){
                if(clientScores.get(username) > highScore){
                    highScore = clientScores.get(username);
                }
            }
            listModel.addElement(String.format("%-32s%5s", username, clientScores.get(username)));
        }
        if(winning){
            System.out.println("Winners are: ");
            for (Object user:
                 CommonUtils.findKeyFromValue(clientScores, highScore)) {
                System.out.println(user.toString());
            }
        }
    }




    public void show(){
        frame.setVisible(true);
    }

    public void hide(){
        frame.setVisible(false);
    }
}
