package mistaomega.jahoot.gui;

import mistaomega.jahoot.SerializeUtils;
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
        initListeners();
    }

    /**
     * listeners set here
     */
    public void initListeners(){
        btnAddQuestion.addActionListener(e -> {
            addQuestion();
        });
        btnSubmitQuestions.addActionListener(e -> {
            submitQuestions();
        });
    }
    public void addQuestion() {
        if (lstQuestions.getModel().getSize() == 0) {
            DefaultListModel<Question> listModel = new DefaultListModel<>();
            lstQuestions.setModel(listModel);
        }
        DefaultListModel<Question> listModel = (DefaultListModel<Question>) lstQuestions.getModel();


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
        int CorrectAnswer = CorrectComboBox.getSelectedIndex(); // 0, 1, 2, or 3

        Question toAdd = new Question(QuestionName, Choices, CorrectAnswer);
        Questions.add(toAdd);
        listModel.addElement(toAdd);

        // Empty all fields after use
        tfQuestionTitle.setText("");
        tfAns1.setText("");
        tfAns2.setText("");
        tfAns3.setText("");
        tfAns4.setText("");
        CorrectComboBox.setSelectedIndex(0);


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

        SerializeUtils.SerializeQuestion(Questions, tfQuestionBankTitle.getText() + ".qbk");
    }

}
