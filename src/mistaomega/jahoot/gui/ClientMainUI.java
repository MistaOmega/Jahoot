package mistaomega.jahoot.gui;

import mistaomega.jahoot.client.Client;
import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Timer;
import java.util.*;

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
    private JLabel lblAnswer1;
    private JLabel lblAnswer3;
    private JLabel lblAnswer4;
    private JButton btnAnswer2;
    private boolean questionAnswered;
    private int givenAnswerIndex;

    public ClientMainUI(Client client) {
        this.client = client;
        initListeners();
    }

    /**
     * listeners set here
     */
    public void initListeners() {
        btnAnswer2.addActionListener(e -> {
            answerQuestion(0);
        });
    }

    public void run(ObjectInputStream objectInputStream) {
        JFrame frame = new JFrame("Main GUI");
        frame.setContentPane(new ClientMainUI(client).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try {
            playGame(objectInputStream);
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

    public void addQuestion(String question) {
        SwingUtilities.invokeLater(() -> {
            btnAnswer2.setText(question);
            lblAnswer1.setText(question);
            tfQuestion.validate();
            btnAnswer2.repaint();
            btnAnswer2.revalidate();
            lblAnswer1.repaint();
            lblAnswer1.revalidate();
            for (JPanel panel :
                    panels) {
                panel.repaint();
                panel.revalidate();
            }
            mainPanel.repaint();
            mainPanel.revalidate();
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

    public void playGame(ObjectInputStream objectIn) throws IOException, ClassNotFoundException {
        Question question = (Question) objectIn.readObject();
        System.out.println(question.getQuestionName());

        addQuestion(question.getQuestionName()); //TODO not working for some reason

        List<String> answers = Arrays.asList(question.getQuestionChoices());
        String correct = answers.get(question.getCorrect());
        Collections.shuffle(answers);
        // send here
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 30000;

            public void run() {
                i -= 1;
                if (questionAnswered) { // If statement triggered if question is answered before the timer runs out
                    timer.cancel();
                    System.out.println("Entry 1");
                    try {
                        client.checkAnswer(i, answers, correct);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (i < 0) { // triggered if the timer runs out
                    timer.cancel();
                    try {
                        client.checkAnswer(i, answers, correct);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, 0, 1000);

    }

    public void answerQuestion(int index) {
        givenAnswerIndex = index;
        questionAnswered = true;
    }

}
