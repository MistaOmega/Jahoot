package mistaomega.jahoot;

import mistaomega.jahoot.gui.QuestionsUI;
import mistaomega.jahoot.gui.ServerGUI;

import javax.swing.*;

public class Main {
    static JFrame frame;

    /**
     * This class makes the look of the UI look like that of the host machine
     * For example. running Windows 10, will make the UI look like a Windows 10 UI, same applies for MacOS or Linux
     */
    public static void setLookAndFeel(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        setLookAndFeel();

        Object[] options = {"Create Questions",
                "Run Server"};
        int n = JOptionPane.showOptionDialog(frame,
                "Would you to create a question bank, or run the server?",
                "Setup",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]); //default button title
        System.out.println(n);

        if (n == 0) {
            QuestionsUI questionsUI = new QuestionsUI();
            questionsUI.run();
        } else {
            ServerGUI serverGUI = new ServerGUI();
            serverGUI.run();
        }

    }
}
