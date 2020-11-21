package mistaomega.jahoot;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.QuestionsUI;
import mistaomega.jahoot.gui.ServerGUI;

import javax.swing.*;

/**
 * Entry class for the program
 * @author Jack Nash
 * @version 1.0
 */
public class Main {
    static JFrame frame;

    /**
     * This class makes the look of the UI look like that of the host machine
     * For example. running Windows 10, will make the UI look like a Windows 10 UI, same applies for MacOS or Linux
     */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Entry point
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        setLookAndFeel();

        Object[] options = {"Create Questions",
                "Run Client",
                "Run Server"};
        int option = JOptionPane.showOptionDialog(frame,
                "Would you to create a question bank, run the client or host the server?",
                "Setup",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,  // Button titles
                options[0]); // default button title
        // Option determines which window to run.
        switch (option) {
            case 0:
                QuestionsUI questionsUI = new QuestionsUI();
                questionsUI.run();
                break;
            case 1:
                ClientConnectUI clientConnectUI = new ClientConnectUI();
                clientConnectUI.run();
                break;
            case 2:
                ServerGUI serverGUI = new ServerGUI();
                serverGUI.run();
                break;
            default:
                System.exit(0);
        }

    }
}
