package mistaomega.jahoot.gui;

import mistaomega.jahoot.lib.CommonUtils;
import mistaomega.jahoot.server.ClientHandler;
import mistaomega.jahoot.server.JahootServer;
import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class ServerGUI extends UserInterfaceControllerClass {
    private static JahootServer jahootServer;
    private JList<ClientHandler> lstUsers;
    private JPanel mainPanel;
    private JButton btnReady;
    private JButton btnStartServer;
    private JButton btnRemoveUser;
    private JButton btnExit;

    public ServerGUI() {
        super(new JFrame("Server GUI"));
        initListeners();

    }

    /**
     * All listeners for buttons are done here
     */
    public void initListeners() {
        btnExit.addActionListener(e -> System.exit(0));
        btnReady.addActionListener(e -> setReadyToPlay());
        btnStartServer.addActionListener(e -> beginConnectionHandle());
        btnRemoveUser.addActionListener(e -> {
            if (lstUsers.getModel().getSize() == 0) {
                return;
            }
            jahootServer.removeUser(lstUsers.getSelectedValue().getUsername(), lstUsers.getSelectedValue());
            removeFromUsers(lstUsers.getSelectedValue());
        });
    }

    /**
     * Entry function for the UI
     */
    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(new ServerGUI().mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

    }

    public void setReadyToPlay() {
        jahootServer.setReadyToPlay(true);
    }


    public void beginConnectionHandle() {

        String directory = new File("").getAbsolutePath(); // get current working directory, should make sure I can get question banks!
        File f = new File(directory);
        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".qbk");
        File[] files = f.listFiles(textFilter);

        if (files.length == 0) {
            JOptionPane.showMessageDialog(mainPanel, "No question bank files can be found, please make a question bank first", "No question banks found", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //TODO implement FileChooser


        final JComboBox<File> combo = new JComboBox<>(files);
        String[] options = {"OK", "Cancel"};
        String title = "Title";
        int selection = JOptionPane.showOptionDialog(null, combo, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                options[0]);
        // If cancel is selected
        if (selection == 1) {
            return;
        }

        ArrayList<Question> questions = CommonUtils.DeserializeQuestion((File) combo.getSelectedItem());
        assert questions != null;
        for (Question q :
                questions) {
            System.out.println(q.getQuestionName());
        }

        int port;
        String portStr = JOptionPane.showInputDialog("Enter Port");
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainPanel, "Port parse failed. Enter a number for the port", "Port parse error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            jahootServer = new JahootServer(port, this, questions); // Boot up an instance of the server here
            jahootServer.run();
        }).start();

    }

    /**
     * adds new user to the users list so that the server owner can see who's connected
     *
     * @param client Client to add to the list model
     */
    public void addToUsers(ClientHandler client) {
        if (lstUsers.getModel().getSize() == 0) { // check if the model for the list is empty
            DefaultListModel<ClientHandler> defaultListModel = new DefaultListModel<>(); // create new list model
            lstUsers.setModel(defaultListModel); // set list model to the new defaultListModel
            btnRemoveUser.setEnabled(true);
        }
        DefaultListModel<ClientHandler> defaultListModel = (DefaultListModel<ClientHandler>) lstUsers.getModel(); // Need to cast to a parameterised version of the DefaultListModel to add later
        defaultListModel.addElement(client);
        if(!btnReady.isEnabled()){
            btnReady.setEnabled(true);
        }
    }

    /**
     * This function will remove the given clienthandler from the clients' list
     *
     * @param client ClientHandler to remove
     */
    public void removeFromUsers(ClientHandler client) {
        DefaultListModel<ClientHandler> defaultListModel = (DefaultListModel<ClientHandler>) lstUsers.getModel(); // Need to cast to a parameterised version of the DefaultListModel to add later
        defaultListModel.removeElement(client);
        if(defaultListModel.isEmpty()){
            btnReady.setEnabled(false);
        }

    }

    public void clearAllClients() {
        DefaultListModel<ClientHandler> defaultListModel = (DefaultListModel<ClientHandler>) lstUsers.getModel(); // Need to cast to a parameterised version of the DefaultListModel to add later
        defaultListModel.removeAllElements();
        if(defaultListModel.isEmpty()){
            btnReady.setEnabled(false);
        }
    }

}
