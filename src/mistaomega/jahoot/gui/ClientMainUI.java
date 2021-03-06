package mistaomega.jahoot.gui;

import mistaomega.jahoot.client.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This is the gameplay UI
 * The user will select a button within the allotted time
 * This controller will send the answer selected back to the client class, which then processes the response
 *
 * @author Jack Nash
 * @version 1.0
 */
public class ClientMainUI extends UserInterfaceControllerClass {
    private final Client client;
    private ArrayList<String> colorList;
    private ArrayList<JPanel> panels;
    private JPanel mainPanel;
    private JPanel answerPane1;
    private JPanel answerPane2;
    private JPanel answerPane3;
    private JPanel answerPane4;
    private JTextField tfQuestion;
    private JButton btnAnswer2;
    private JButton btnAnswer1;
    private JButton btnAnswer3;
    private JButton btnAnswer4;
    private JTextField tfTimeLeft;

    /**
     * constructor
     *
     * @param client Instance of the Client
     */
    public ClientMainUI(Client client) {
        super(new JFrame("Game Interface"));
        this.client = client;
        initListeners();
        btnAnswer1.addActionListener(e -> client.answerQuestion(0));
    }

    /**
     * listeners set here
     */
    public void initListeners() {
        btnAnswer1.addActionListener(e -> client.answerQuestion(0));
        btnAnswer2.addActionListener(e -> client.answerQuestion(1));
        btnAnswer3.addActionListener(e -> client.answerQuestion(2));
        btnAnswer4.addActionListener(e -> client.answerQuestion(3));
    }

    /**
     * Entry function
     */
    public void run() {
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    /**
     * Randomly sets the button panel colours
     */
    public void setPanelColors() {
        Random rnd = new Random();
        for (JPanel panel : panels) {
            int index = rnd.nextInt(colorList.size());
            panel.setBackground(Color.decode(colorList.get(index)));
            colorList.remove(index);
        }
    }

    /**
     * Adds information to the question buttons
     *
     * @param questionTitle Title of the Question
     * @param answers       List of answers to the question
     */
    public void addQuestion(String questionTitle, List<String> answers) {
        SwingUtilities.invokeLater(() -> {
            tfQuestion.setText(questionTitle);
            btnAnswer1.setText(answers.get(0));
            btnAnswer2.setText(answers.get(1));
            btnAnswer3.setText(answers.get(2));
            btnAnswer4.setText(answers.get(3));
        });

    }

    /**
     * Sets remaining time left
     *
     * @param timeLeft How much time is left
     */
    public void setTfTimeLeft(String timeLeft) {
        tfTimeLeft.setText("Time left: " + timeLeft);
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
