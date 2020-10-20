package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.JahootServer;

import javax.swing.*;

public class QuestionsUI {
    private JPanel mainPanel;
    private JTextField tfQuestionTitle;
    private JTextField tfAns1;
    private JTextField tfAns2;
    private JTextField tfAns3;
    private JTextField tfAnsCorrect;
    private JButton btnAddQuestion;
    private JButton btnSubmitQuestions;
    private JList lstQuestions;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void run() {
        JFrame frame = new JFrame("Questions GUI");
        frame.setContentPane(new QuestionsUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
