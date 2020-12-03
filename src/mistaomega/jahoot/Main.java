package mistaomega.jahoot;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.QuestionsUI;
import mistaomega.jahoot.gui.ServerGUI;
import mistaomega.jahoot.lib.Config;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Entry class for the program
 *
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

    public static void writeConfig() {
        Properties prop = new Properties();
        try {
            FileOutputStream out = new FileOutputStream("jahoot.properties");
            // set the properties value
            prop.setProperty("jahoot.questionPath", "\\");
            prop.setProperty("jahoot.port", "5000");
            prop.setProperty("jahoot.host", "localhost");
            prop.setProperty("jahoot.username", "");

            // save properties to project root folder
            prop.store(out, "--- Jahoot Properties File --- \n" +
                    "questionPath is the path extension from the root path, I.E \\questions would be in the questions folder ahead of where this file is \n" +
                    "port is the default port to use the application through, port forward this port, you can change this in the serverGUI too! \n" +
                    "host is the hostname you want to connect through \n" +
                    "username is the username you wish to play as by default");
            out.close();

            System.out.println(prop);
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

        // write new config if none exists
        Config config = Config.getInstance();
        if(config == null){
            writeConfig();
        }

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
