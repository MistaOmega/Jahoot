package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class QuestionsUI {
    private final ArrayList<Question> Questions = new ArrayList<>();
    private JPanel mainPanel;
    private JTextField tfQuestionTitle;
    private JTextField tfAns1;
    private JTextField tfAns2;
    private JTextField tfAns3;
    private JTextField tfAns4;
    private JButton btnAddQuestion;
    private JButton btnSubmitQuestions;
    private JList<Question> lstQuestions; //TODO Change wildcard for question object when properly implemented
    private JScrollPane scrollList;
    private JTextField tfQuestionBankTitle;
    private JComboBox<String> CorrectComboBox;

    public QuestionsUI() {
        btnAddQuestion.addActionListener(e -> {
            addQuestion();
        });
        btnSubmitQuestions.addActionListener(e -> {
            submitQuestions();
        });
    }

    public static void serializeQuestions(ArrayList<Question> questions, String filename) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(questions);
        oos.close();
    }

    public void addQuestion() {
        if (lstQuestions.getModel().getSize() == 0) {
            DefaultListModel<Question> listModel = new DefaultListModel<>();
            lstQuestions.setModel(listModel);
        }
        DefaultListModel<Question> listModel = (DefaultListModel<Question>) lstQuestions.getModel();
        listModel.addElement(new Question("Hello", new String[0], 1));


        if (tfQuestionTitle.getText().isEmpty() || tfAns1.getText().isEmpty()
                || tfAns2.getText().isEmpty() || tfAns3.getText().isEmpty()
                || tfAns4.getText().isEmpty()) {
            JOptionPane.showConfirmDialog(mainPanel, "Please fill out all fields");
            return;
        }

        String QuestionName = tfQuestionTitle.getText();
        String[] Choices = new String[4];
        Choices[0] = tfAns1.getText();
        Choices[1] = tfAns2.getText();
        Choices[2] = tfAns3.getText();
        Choices[3] = tfAns4.getText();
        char CorrectAnswer = 'D';

        Question toAdd = new Question(QuestionName, Choices, CorrectAnswer);
        Questions.add(toAdd);


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

    public void submitQuestions() {

        if (Questions.isEmpty()) {
            JOptionPane.showConfirmDialog(mainPanel, "Question bank empty");
            return;
        }

        if (tfQuestionBankTitle.getText().isEmpty()) {
            JOptionPane.showConfirmDialog(mainPanel, "Add a title for the question bank");
            return;
        }

        try {
            serializeQuestions(Questions, tfQuestionBankTitle.getText() + ".qbk");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
