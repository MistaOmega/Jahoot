package mistaomega.jahoot.gui;

import mistaomega.jahoot.lib.CommonUtils;
import mistaomega.jahoot.lib.Config;
import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * This is the controller for the Question form.
 * It is responsible for handling the creation and management of question banks
 *
 * @author Jack Nash
 * @version 1.0
 */
public class QuestionsUI extends UserInterfaceControllerClass {
    private ArrayList<Question> Questions = new ArrayList<>();
    private JPanel mainPanel;
    private JTextField tfQuestionTitle;
    private JTextField tfAns1;
    private JTextField tfAns2;
    private JTextField tfAns3;
    private JTextField tfAns4;
    private JButton btnAddQuestion;
    private JButton btnSubmitQuestions;
    private JList<Question> lstQuestions;
    private JTextField tfQuestionBankTitle;
    private JComboBox<String> CorrectComboBox;
    private JComboBox<File> existingBankBox;
    private JCheckBox editMode;
    private JButton btnDeleteList;
    private Config config;

    public QuestionsUI() {
        super(new JFrame("Questions Interface"));
        config = Config.getInstance();
        initListeners();

        btnDeleteList.addActionListener(e -> deleteItem());
    }

    /**
     * listeners set here
     */
    @SuppressWarnings("unchecked")
    // an unchecked cast is present at "(JList<Question>)event.getSource()" as this list is always a list of questions, the cast is fine
    private void initListeners() {
        // This listener is made in part from the following resource: https://stackoverflow.com/a/13800817
        // I not used this in verbatim, I have modified it appropriately to suit my needs
        lstQuestions.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) { // if done adjusting list, I.E this is the final selection
                JList<Question> source = (JList<Question>) event.getSource();
                Question selected = source.getSelectedValue();

                populateFields(selected);
            }
        });
        btnAddQuestion.addActionListener(e -> addQuestion());
        btnSubmitQuestions.addActionListener(e -> submitQuestions());
        existingBankBox.addActionListener(e -> {
            if (editMode.isSelected()) {
                try {
                    if (!(existingBankBox.getSelectedItem() == null)) {
                        populateBoxes();
                    }
                } catch (NullPointerException nullPointerException) {
                    JOptionPane.showMessageDialog(mainPanel, "Error caught deserializing file", "Serialize error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        editMode.addActionListener(e -> setupEditMode());

    }

    private void addQuestion() {
        if (editMode.isSelected()) {
            editQuestion();
            return;
        }


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

        AddQuestionToList(listModel);

        // Empty all fields after use
        clearFields();


    }

    private void setupEditMode() {
        if (editMode.isSelected()) {
            existingBankBox.setEnabled(true);
            if (existingBankBox.getItemAt(0) == null || existingBankBox.getSelectedItem() == null) {
                return;
            }
            btnDeleteList.setEnabled(true);
            tfQuestionBankTitle.setEnabled(false);
            File f = (File) existingBankBox.getSelectedItem();
            if (f.getName().contains(".")) {
                tfQuestionBankTitle.setText(f.getName().substring(0, f.getName().lastIndexOf('.')));
            }
            populateBoxes();
        } else {
            btnDeleteList.setEnabled(false);
            if (Questions != null) {
                Questions.clear();
            }
            tfQuestionBankTitle.setEnabled(true);
            tfQuestionBankTitle.setText("");
            existingBankBox.setEnabled(false);
            DefaultListModel<Question> defaultListModel = (DefaultListModel<Question>) lstQuestions.getModel();
            defaultListModel.removeAllElements();
            clearFields();
        }
    }

    private void clearFields() {
        tfQuestionTitle.setText("");
        tfAns1.setText("");
        tfAns2.setText("");
        tfAns3.setText("");
        tfAns4.setText("");
        CorrectComboBox.setSelectedIndex(0);
    }

    public void run() {
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        getExistingBanks();
    }

    private void submitQuestions() {

        if (Questions.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Question bank is empty, please add questions first", "Empty bank", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tfQuestionBankTitle.getText().isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "You're missing a question bank title", "No title", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String filePath = "";
        if(config != null && config.containsKey("jahoot.questionPath")){
            filePath = config.getProperty("jahoot.questionPath");
        }
        File directory = new File(System.getProperty("user.dir") + filePath);
        if (! directory.exists()){
            if(!directory.mkdirs()){
                JOptionPane.showMessageDialog(mainPanel, "Error in making question bank directory. You shouldn't be seeing this, try making the folder yourself\n" +
                        "default is \\Questions", "Cannot make directory", JOptionPane.ERROR_MESSAGE);
                return;
            };
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

        File file = new File(System.getProperty("user.dir") + filePath + "/" + tfQuestionBankTitle.getText() + ".qbk");
        CommonUtils.SerializeQuestion(Questions, file);

        JOptionPane.showMessageDialog(mainPanel, "Question bank processed, please proceed to run the server when ready.", "Success!", JOptionPane.INFORMATION_MESSAGE);
        getExistingBanks();
    }

    private void getExistingBanks() {
        String filePath = "";
        if(config != null && config.containsKey("jahoot.questionPath")){
            filePath = config.getProperty("jahoot.questionPath");
        }
        existingBankBox.removeAllItems();
        File[] files = CommonUtils.getQuestionBanks(new File(System.getProperty("user.dir") + filePath).getAbsolutePath()); // get current working directory, should make sure I can get question banks!
        if (files == null || files.length == 0) {
            return;
        }
        if (existingBankBox.getModel().getSize() == 0) {
            DefaultComboBoxModel<File> comboBoxModel = new DefaultComboBoxModel<>();
            existingBankBox.setModel(comboBoxModel);
        }
        DefaultComboBoxModel<File> comboBoxModel = (DefaultComboBoxModel<File>) existingBankBox.getModel();
        for (File f : files) {
            System.out.println(f);
            comboBoxModel.addElement(f);
        }
    }

    private void populateBoxes() throws NullPointerException {
        if (Questions != null) {
            Questions.clear();
        }
        if (lstQuestions.getModel().getSize() == 0) {
            DefaultListModel<Question> listModel = new DefaultListModel<>();
            lstQuestions.setModel(listModel);
        }
        DefaultListModel<Question> listModel = (DefaultListModel<Question>) lstQuestions.getModel();
        listModel.removeAllElements(); // clear list before populating

        Questions = CommonUtils.DeserializeQuestion((File) existingBankBox.getSelectedItem());
        if (Questions == null) {
            return;
        }
        for (Question question : Questions) {
            listModel.addElement(question);
        }

    }

    private void populateFields(Question question) {
        if (question == null) { // this will only happen really, if people mess around with the list
            return;
        }
        tfQuestionTitle.setText(question.getQuestionName());
        tfAns1.setText(question.getQuestionChoices()[0]);
        tfAns2.setText(question.getQuestionChoices()[1]);
        tfAns3.setText(question.getQuestionChoices()[2]);
        tfAns4.setText(question.getQuestionChoices()[3]);
        CorrectComboBox.setSelectedIndex(question.getCorrect());
    }

    private void editQuestion() {
        if (lstQuestions.getModel().getSize() == 0) { // if empty, don't bother
            return;
        }
        DefaultListModel<Question> listModel = (DefaultListModel<Question>) lstQuestions.getModel();
        Questions.remove(lstQuestions.getSelectedValue());
        listModel.removeElement(lstQuestions.getSelectedValue()); // remove existing question

        AddQuestionToList(listModel);
        clearFields();
    }

    private void AddQuestionToList(DefaultListModel<Question> listModel) {
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
    }

    public void deleteItem() {
        if (existingBankBox.getSelectedItem() != null) {
            File f = (File) existingBankBox.getSelectedItem();
            if (f.delete()) {
                existingBankBox.removeItem(f);
            }
        }
    }

}
