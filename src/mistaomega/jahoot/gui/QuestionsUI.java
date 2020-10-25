package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
    private JScrollPane scrollList;

    private final ArrayList<Question> Questions = new ArrayList<>();

    public QuestionsUI() {
        btnAddQuestion.addActionListener(e -> {
            addQuestion();
        });
    }

    public void addQuestion() {
        String QuestionName = tfQuestionTitle.getText();
        String[] Choices = new String[4];
        Choices[0] = tfAns1.getText();
        Choices[1] = tfAns2.getText();
        Choices[2] = tfAns3.getText();
        Choices[3] = tfAnsCorrect.getText();
        char CorrectAnswer = 'D';

        Question toAdd = new Question(QuestionName, Choices, CorrectAnswer);
        Questions.add(toAdd);

        try {
            serializeQuestions(toAdd, "questions.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void serializeQuestions(Object question, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(question);
        oos.close();
    }
}
