package mistaomega.jahoot.gui;

import mistaomega.jahoot.client.Client;
import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class ClientMainUI {
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
    private JButton btnAnswer2;
    private JButton btnAnswer1;
    private JButton btnAnswer3;
    private JButton btnAnswer4;
    private boolean questionAnswered;
    private int givenAnswerIndex;
    private ObjectInputStream objectIn;

    public ClientMainUI(Client client, ObjectInputStream objectInputStream) {
        objectIn = objectInputStream;
        this.client = client;

        btnAnswer2.addActionListener(e -> {
            client.answerQuestion(0);
        });
    }

    public void run() {
        JFrame frame = new JFrame("Main GUI");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 800);
        frame.setVisible(true);
        try {
            client.playGame();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setPanelColors() {
        Random rnd = new Random();
        for (JPanel panel : panels) {
            int index = rnd.nextInt(colorList.size());
            panel.setBackground(Color.decode(colorList.get(index)));
            colorList.remove(index);
        }
    }

    public void addQuestion(Question question) {
        SwingUtilities.invokeLater(() -> {
            tfQuestion.setText(question.getQuestionName());
            List<String> answers = Arrays.asList(question.getQuestionChoices());
            Collections.shuffle(answers);
            btnAnswer1.setText(answers.get(0));
            btnAnswer2.setText(answers.get(1));
            btnAnswer3.setText(answers.get(2));
            btnAnswer4.setText(answers.get(3));
        });

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
