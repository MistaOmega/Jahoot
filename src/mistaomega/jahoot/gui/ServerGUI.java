package mistaomega.jahoot.gui;

import mistaomega.jahoot.server.ClientHandler;
import mistaomega.jahoot.server.JahootServer;

import javax.swing.*;

public class ServerGUI {
    private static JahootServer jahootServer;
    private JList<ClientHandler> lstUsers;
    private JPanel mainPanel;
    private JButton btnReady;
    private JButton btnStartServer;
    private JButton btnRemoveUser;

    public ServerGUI() {
        initListeners();
    }

    /**
     * All listeners for buttons are done here
     */
    public void initListeners() {
        btnReady.addActionListener(e -> setReadyToPlay());
        btnStartServer.addActionListener(e -> beginConnectionHandle());
        btnRemoveUser.addActionListener(e -> {
            if(lstUsers.getModel().getSize() == 0) {
                return;
            }
            removeFromUsers(lstUsers.getSelectedValue());
            jahootServer.removeUser(lstUsers.getSelectedValue().getUsername(), lstUsers.getSelectedValue());
        });
    }
    /**
     * Entry function for the UI
     */
    public void run() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Server GUI");
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
        int port;
        String portStr = JOptionPane.showInputDialog("Enter Port");
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            JOptionPane.showConfirmDialog(mainPanel, "Port parse failed. Enter a number for the port");
            return;
        }

        new Thread(() -> {
            jahootServer = new JahootServer(port, this); // Boot up an instance of the server here
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
    }

}
