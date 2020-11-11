package mistaomega.jahoot.gui;

import mistaomega.jahoot.client.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ClientMainUI extends Thread {
    private final Client client;
    private ArrayList<String> colorList;
    private ArrayList<JPanel> panels;
    private JPanel mainPanel;
    private JPanel answerPane1;
    private JPanel answerPane2;
    private JPanel answerPane3;
    private JPanel answerPane4;
    private JTextField tfTitle;
    private JTextField tfQuestion;
    private JLabel lblAnswer1;
    private JLabel lblAnswer3;
    private JLabel lblAnswer4;
    private JButton btnAnswer2;

    public ClientMainUI(Client client) {
        this.client = client;

        btnAnswer2.addActionListener(e -> {
            client.answerQuestion(0);
        });
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("Main GUI");
        frame.setContentPane(new ClientMainUI(client).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public void setPanelColors() {
        Random rnd = new Random();
        for (JPanel panel : panels) {
            int index = rnd.nextInt(colorList.size());
            panel.setBackground(Color.decode(colorList.get(index)));
            colorList.remove(index);
        }
    }

    public void addQuestion(String question) {
        btnAnswer2.setText(question);
        lblAnswer1.setText(question);
        tfQuestion.validate();
        tfQuestion.repaint();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        colorList = new ArrayList<>(Arrays.asList(JahootColors.JAHOOTBLUE.getHex(), JahootColors.JAHOOTLIME.getHex(), JahootColors.JAHOOTORANGE.getHex(), JahootColors.JAHOOTPINK.getHex()));
        panels = new ArrayList<>();
        answerPane1 = new JPanel();
        answerPane2 = new JPanel();
        answerPane3 = new JPanel();
        answerPane4 = new JPanel();
        panels.add(answerPane1);
        panels.add(answerPane2);
        panels.add(answerPane3);
        panels.add(answerPane4);
        setPanelColors();

    }


}
