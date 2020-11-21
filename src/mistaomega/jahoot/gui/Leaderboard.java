package mistaomega.jahoot.gui;

import mistaomega.jahoot.lib.CommonUtils;
import mistaomega.jahoot.server.ClientHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Leaderboard extends UserInterfaceController{
    private JList<String> scoresList;
    private JPanel mainPanel;
    private JTextField LEADERBOARDSTextField;

    public Leaderboard() {
        super(new JFrame("Leaderboard"));
    }

    /**
     * Entry function for the UI
     */
    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setSize(1000, 1000);
            frame.setVisible(false);
        });

        scoresList.setBackground(Color.decode(JahootColors.JAHOOTBLUE.getHex()));
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
        Map<String, Integer> result = clientScores.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (String username :
                result.keySet()) {
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
