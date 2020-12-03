package mistaomega.jahoot.gui;

import mistaomega.jahoot.lib.CommonUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the leaderboard class
 * It is the controller for the Leaderboard form
 * It is responsible for displaying the latest scores to the users
 */
public class Leaderboard extends UserInterfaceControllerClass {
    private JList<String> scoresList;
    private JPanel mainPanel;

    /**
     * Constructor
     */
    public Leaderboard() {
        super(new JFrame("Leaderboard"));
    }

    /**
     * Entry function for the UI
     */
    @Override
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

    /**
     * This sets the latest scores on the leaderboards
     *
     * @param clientScores map of all client scores and their linked user
     * @param winning      if the game is over
     * @return a string of the winners if the game is over
     */
    public String displayLatestScores(Map<String, Integer> clientScores, boolean winning) {
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


        for (String username : result.keySet()) {
            if (winning) {
                if (result.get(username) > highScore) {
                    highScore = result.get(username);
                }
            }

            listModel.addElement(String.format("%-32s%5s", username, result.get(username)));
        }
        if (winning) {
            StringBuilder winners = new StringBuilder();
            for (Object user :
                    CommonUtils.findKeyFromValue(clientScores, highScore)) {
                winners.append(user.toString()).append(" ");
            }
            return winners.toString();
        }
        return null;
    }

}
